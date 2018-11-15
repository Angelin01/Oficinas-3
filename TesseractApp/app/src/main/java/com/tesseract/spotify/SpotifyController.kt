package com.tesseract.spotify

import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.tesseract.communication.TesseractCommunication
import com.tesseract.spotify.SpotifyConnectionValues
import com.tesseract.spotify.SpotifyHTTPRequests

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothMessageCallback
import com.tesseract.communication.TesseractCommunication

class SpotifyController: ViewModel(), BluetoothMessageCallback {
	override fun callbackMessageReceiver(values: Any, subtype: String?) {
		val gson = Gson()
		when (subtype) {
			"list" -> {
				this.spotifyPlayList.postValue(getSpotifyPlaylist(values as ArrayList<String>))
			}

		}
	}

	var spotifyPlayList: MutableLiveData<List<SpotifyPlaylist>> = MutableLiveData()

	init {
		spotifyPlayList.value = ArrayList()
		TesseractCommunication.spotifyPlaylistListener = this
	}

	fun searchPlaylist(search: String?) {
		spotifyPlayList.value = foundSpotifyPlaylist(search)
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

	private fun foundSpotifyPlaylist(search: String?): List<SpotifyPlaylist> {
		this.requestSpotifyPlaylist(search)
		val gson = Gson()
		val spotifyPlaylist: List<SpotifyPlaylist> = gson.fromJson(sampleSpotifyPlaylist, Array<SpotifyPlaylist>::class.java).toList()
		return spotifyPlaylist
	}

	// TODO: Convert search to json
	private fun requestSpotifyPlaylist(search: String?) {
		if (search == null) {
			TesseractCommunication.sendRequest("spotify", "search-playlist", "null")
			return
		}

		TesseractCommunication.sendRequest("spotify", "search-playlist", search)
	}


	private fun getSpotifyPlaylist(values: ArrayList<String>): List<SpotifyPlaylist> {
		val gson = Gson()
		val spotifyPlaylist: ArrayList<SpotifyPlaylist> = ArrayList()
		for (playlistFound: Any in values) {
			spotifyPlaylist.add(gson.fromJson(gson.toJson(playlistFound), SpotifyPlaylist::class.java))
		}
		return spotifyPlaylist
	}

	companion object {
		val clientID = "fbd9312c3e1e4942ac05ef1012776736"
		val redirectURI = "com.tesseract.app://callback"
		val deviceName = "Tesseract"

		var deviceID: String = ""
		var token: String = ""

		var isActive: Boolean = false

		fun setSpotifyConnection(activity: FragmentActivity?, token: String)
		{
			this.token = token
			deviceID = SpotifyHTTPRequests.getDeviceID()
			TesseractCommunication.sendRequest("spotify", "connect", Gson().toJsonTree(SpotifyConnectionValues(this.token, this.deviceID)))

			isActive = true

			//region Testes
			previousTrack()
			nextTrack()
			pause()
			resume()
			//endregion
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
	}
}