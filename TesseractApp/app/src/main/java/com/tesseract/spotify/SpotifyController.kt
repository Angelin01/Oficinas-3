package com.tesseract

import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.tesseract.communication.TesseractCommunication
import com.tesseract.spotify.SpotifyConnectionValues
import com.tesseract.spotify.SpotifyHTTPRequests

class SpotifyController() {

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
