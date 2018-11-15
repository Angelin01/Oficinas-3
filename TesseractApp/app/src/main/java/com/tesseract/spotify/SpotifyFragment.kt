package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.widget.Button
import android.view.View
import android.view.ViewGroup

class SpotifyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view: View = inflater.inflate(R.layout.fragment_spotify, container, false)
        val buttonSpotifyConnect: Button = view.findViewById(R.id.buttonSpotifyConnect)
        buttonSpotifyConnect.setOnClickListener { SpotifyController.requestSpotifyToken(this.activity) }
        return view
    }
}
