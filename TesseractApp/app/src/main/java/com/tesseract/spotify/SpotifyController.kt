package com.tesseract.spotify

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.tesseract.MainActivity
import com.tesseract.communication.TesseractCommunication
import com.tesseract.music.Music
import com.tesseract.music.MusicController

class DeviceDisconnectedException(message:String): Exception(message)

class SpotifyController: ViewModel() {

	var spotifyPlayList: MutableLiveData<List<SpotifyPlaylist>> = MutableLiveData()

	init {
		spotifyPlayList.value = ArrayList()
	}

	fun searchPlaylist(search: String?) {
		spotifyPlayList.value = findSpotifyPlaylist(search)
	}

	private fun findSpotifyPlaylist(search: String?): ArrayList<SpotifyPlaylist> {
        var spotifyPlaylists: ArrayList<SpotifyPlaylist> = ArrayList()

        val userPlaylistsJsonArray = SpotifyHTTPRequests.getUserPlaylists().get("items").asJsonArray
        for (element in userPlaylistsJsonArray)
        {
            try
            {
                val userPlaylistJson = element.asJsonObject

                var userPlaylistName = userPlaylistJson.get("name").asString
                var musicQuantity = userPlaylistJson.get("tracks").asJsonObject.get("total").asInt
                var userPlaylistCoverUri = ""

                val userPlaylistCoversJsonArray = userPlaylistJson.get("images").asJsonArray
                var userPlaylistCoverSize = 0
                for (element in userPlaylistCoversJsonArray)
                {
                    val userPlaylistCoverJson = element.asJsonObject
                    if (userPlaylistCoverJson.get("height").asInt > userPlaylistCoverSize)
                    {
                        userPlaylistCoverSize = userPlaylistCoverJson.get("height").asInt
                        userPlaylistCoverUri = userPlaylistCoverJson.get("url").asString
                    }
                }

                if (search == null || search == "" || search in userPlaylistName)
                {
                    val spotifyPlaylist = SpotifyPlaylist(name = userPlaylistName, playlist_cover_URI = userPlaylistCoverUri, music_quantity = musicQuantity)
                    spotifyPlaylists.add(spotifyPlaylist)
                }
            }
            catch (e: Exception)
            {
                //
            }
        }

        return spotifyPlaylists
	}

	fun selectPlaylist(playListName: String) {
		// spotify.playPlaylist(playListName)
	}

	companion object {
		val clientID = "fbd9312c3e1e4942ac05ef1012776736"
		val redirectURI = "com.tesseract.app://callback"
		val deviceName = "tesseract"

		var deviceID: String = ""
		var token: String = ""

		var isActive: Boolean = false
		lateinit var musicControllerListener: MusicController

		fun setSpotifyConnection(token: String)
		{
			this.token = token
			deviceID = SpotifyHTTPRequests.getDeviceID()
			TesseractCommunication.sendRequest("spotify", "connect", Gson().toJsonTree(SpotifyConnectionValues(this.token, this.deviceID)))

			isActive = true
		}

		fun requestSpotifyToken(activity: FragmentActivity?)
		{
			val builder = AuthenticationRequest.Builder(clientID, AuthenticationResponse.Type.TOKEN, redirectURI)
			builder.setScopes(arrayOf("streaming", "user-read-playback-state", "user-modify-playback-state", "playlist-read-private"))
			val request = builder.build()
			AuthenticationClient.openLoginActivity(activity, MainActivity.spotifyRequestCode, request)
		}

		private fun getCurrentMusic(): Music {
			val playbackInfoJson = SpotifyHTTPRequests.getPlaybackInfo()
			if (playbackInfoJson.get("device").asJsonObject.get("id").asString != deviceID)
				throw DeviceDisconnectedException("Tesseract not connected")

			val musicInfoJson = playbackInfoJson.get("item").asJsonObject
			val musicName = musicInfoJson.get("name").asString

			var artists = ""
			val artistJsonArray = musicInfoJson.get("artists").asJsonArray
			for (element in artistJsonArray)
			{
				if (artists != "")
					artists += ", "
				val artistJson = element.asJsonObject
				artists += artistJson.get("name").asString
			}

			val albumCoversJsonArray = musicInfoJson.get("album").asJsonObject.get("images").asJsonArray
			var albumCoverSize = 0
			var albumCoverURL = ""
			for (element in albumCoversJsonArray)
			{
				val albumCoverJson = element.asJsonObject
				if (albumCoverJson.get("height").asInt > albumCoverSize)
				{
					albumCoverSize = albumCoverJson.get("height").asInt
					albumCoverURL = albumCoverJson.get("url").asString
				}
			}

			val duration = musicInfoJson.get("duration_ms").asInt

			return Music(musicName, artists, albumCoverURL, duration)
		}

		fun sendCommand(command: String) {
			when (command)
			{
				"next" -> SpotifyHTTPRequests.postPlaylistNavigationCommand("next")
				"previous" -> SpotifyHTTPRequests.postPlaylistNavigationCommand("previous")
				"pause" -> SpotifyHTTPRequests.putPlaylistNavigationCommand("pause")
				"play" -> SpotifyHTTPRequests.putPlaylistNavigationCommand("play")
				"shuffle" -> SpotifyHTTPRequests.putPlaylistNavigationCommand("shuffle") //TODO: Special case, needs parameter
			}

			TesseractCommunication.sendRequest("spotify", "command", command)

			when (command)
			{
				"next", "previous" ->
				{
					try
					{
						Thread.sleep(500) //TODO: Find better solution to wait for server to update information.
						val music: Music = getCurrentMusic()
						musicControllerListener.callbackMusisChange(music)
					}
					catch (e: DeviceDisconnectedException)
					{
						return
					}
				}
			}
		}

		fun callbackMessageReceiver(value: Any, subtype: String)
		{
			if (subtype == "command")
			{
				when (value)
				{
					"next", "previous", "play", "pause", "shuffle" ->
					{
						try
						{
							val music: Music = getCurrentMusic()
							musicControllerListener.callbackMusisChange(music)
						}
						catch (e: DeviceDisconnectedException)
						{
							return
						}
					}
				}
			}
		}
	}
}