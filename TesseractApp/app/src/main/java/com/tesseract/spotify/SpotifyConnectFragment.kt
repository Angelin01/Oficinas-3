package com.tesseract.spotify


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.google.gson.Gson
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.tesseract.MainActivity

import com.tesseract.R
import com.tesseract.communication.TesseractCommunication

class SpotifyConnectFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_spotify_connect, container, false)

		val buttonSpotifyConnect: RelativeLayout = view.findViewById(R.id.buttonSpotifyConnect)
		buttonSpotifyConnect.setOnClickListener { buttonSpotifyConnectOnClick() }

		return view
	}

	private fun buttonSpotifyConnectOnClick() {
		val requestCode = 1337
		val clientID = "fbd9312c3e1e4942ac05ef1012776736"
		val redirectURI = "com.tesseract.app://callback"
		val builder = AuthenticationRequest.Builder(clientID, AuthenticationResponse.Type.TOKEN, redirectURI)
		builder.setScopes(Array(1) { "streaming" })
		val request = builder.build()
		AuthenticationClient.openLoginActivity(this.activity, requestCode, request)
	}


}
