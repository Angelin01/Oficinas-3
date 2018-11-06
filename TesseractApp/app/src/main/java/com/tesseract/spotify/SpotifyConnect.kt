package com.tesseract.spotify

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.tesseract.R

class SpotifyConnect : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_spotify_connect, container, false)
        val buttonSpotifyConnect: Button = view.findViewById(R.id.buttonSpotifyConnect)
        buttonSpotifyConnect.setOnClickListener { buttonSpotifyConnectOnClick(view) }
		return view
	}

    private fun buttonSpotifyConnectOnClick(view: View) {
        val editTextSpotifyName: EditText = view.findViewById(R.id.editTextSpotifyName)
        val editTextSpotifyPassword: EditText = view.findViewById(R.id.editTextSpotifyPassword)
        editTextSpotifyName.text.toString()
        editTextSpotifyPassword.text.toString()
    }
}
