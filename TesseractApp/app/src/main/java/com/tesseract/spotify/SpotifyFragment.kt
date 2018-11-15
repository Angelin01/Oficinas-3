package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.widget.Button
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.tesseract.communication.TesseractCommunication
import com.tesseract.spotify.SpotifyConnectionValues

class SpotifyFragment : Fragment() {

    companion object {
        fun sendSpotifyConnectionRequest()
        {
            TesseractCommunication.sendRequest("spotify", "connect", Gson().toJsonTree(SpotifyConnectionValues(MainActivity.spotifyToken)))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_spotify, container, false)
        val buttonSpotifyConnect: Button = view.findViewById(R.id.buttonSpotifyConnect)
        buttonSpotifyConnect.setOnClickListener { buttonSpotifyConnectOnClick (view) }
        return view
    }

    private fun buttonSpotifyConnectOnClick(view: View)
    {
        val requestCode = 1337
        val clientID = "fbd9312c3e1e4942ac05ef1012776736"
        val redirectURI = "com.tesseract.app://callback"
        val builder = AuthenticationRequest.Builder(clientID, AuthenticationResponse.Type.TOKEN, redirectURI)
        builder.setScopes(Array(1) {"streaming"})
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this.activity, requestCode, request)
    }
}
