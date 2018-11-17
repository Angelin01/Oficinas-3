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

class SpotifyController: ViewModel() {

	var spotifyPlayList: MutableLiveData<List<SpotifyPlaylist>> = MutableLiveData()

	init {
		spotifyPlayList.value = ArrayList()
	}

	fun searchPlaylist(search: String?) {
		spotifyPlayList.value = findSpotifyPlaylist(search)
	}

	private val sampleSpotifyPlaylist: String = """[
    {
      "name":"Cat Metal",
      "playlist_cover_URI":"http://i65.tinypic.com/mvno5e.jpg",
      "music_quantity":10
    },
    {
      "name":"Blues",
      "playlist_cover_URI":"http://petitecurie.com/wp-content/uploads/2013/08/521888_679515475396424_1593317559_n.jpg",
      "music_quantity":25
    }
]"""

	private fun findSpotifyPlaylist(search: String?): List<SpotifyPlaylist> {
		this.requestSpotifyPlaylist(search)
		val gson = Gson()
		val spotifyPlaylist: List<SpotifyPlaylist> = gson.fromJson(sampleSpotifyPlaylist, Array<SpotifyPlaylist>::class.java).toList()
		return spotifyPlaylist
	}

	// TODO: Convert to a request to API
	private fun requestSpotifyPlaylist(search: String?) {
		if (search == null) {
			TesseractCommunication.sendRequest("spotify", "search-playlist", "null")
			return
		}

		TesseractCommunication.sendRequest("spotify", "search-playlist", search)
	}


	// TODO: Convert to a request to API
	private fun getSpotifyPlaylist(values: ArrayList<String>): List<SpotifyPlaylist> {
		val gson = Gson()
		val spotifyPlaylist: ArrayList<SpotifyPlaylist> = ArrayList()
		for (playlistFound: Any in values) {
			spotifyPlaylist.add(gson.fromJson(gson.toJson(playlistFound), SpotifyPlaylist::class.java))
		}
		return spotifyPlaylist
	}

	fun selectPlaylist(playListName: String) {
		// spotify.playPlaylist(playListName)
	}

	companion object {
		val clientID = "fbd9312c3e1e4942ac05ef1012776736"
		val redirectURI = "com.tesseract.app://callback"
		val deviceName = "Tesseract"

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
			builder.setScopes(Array(3) {"streaming"; "user-read-playback-state"; "user-modify-playback-state"})
			val request = builder.build()
			AuthenticationClient.openLoginActivity(activity, MainActivity.spotifyRequestCode, request)
		}

		fun nextTrack()
		{
			SpotifyHTTPRequests.postPlaylistNavigationCommand("next")
			val music: Music = getCurrentMusic()
			musicControllerListener.callbackMusisChange(music)
		}

		private fun getCurrentMusic(): Music {
			return Music("sample", "sample band", "https://proxy.duckduckgo.com/iur/?f=1&image_host=https%3A%2F%2F40.media.tumblr.com%2F5fad4aa35c3902a4fe09afa75112e33d%2Ftumblr_nf7cxglVpL1tlvkqao1_500.jpg&u=https://78.media.tumblr.com/5fad4aa35c3902a4fe09afa75112e33d/tumblr_nf7cxglVpL1tlvkqao1_500.jpg", 60)
		}

		fun previousTrack()
		{
			SpotifyHTTPRequests.postPlaylistNavigationCommand("previous")
		}

		fun pause()
		{
			SpotifyHTTPRequests.postPlaylistNavigationCommand("pause")
		}

		fun resume()
		{
			SpotifyHTTPRequests.postPlaylistNavigationCommand("play")
		}

		fun sendCommand(command: String) {

		}
	}
}