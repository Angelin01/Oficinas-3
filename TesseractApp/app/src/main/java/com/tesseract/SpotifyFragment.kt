package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.widget.Button
import android.view.View
import android.view.ViewGroup

class SpotifyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_spotify, container, false)
        configureButtonConnect(view)
        return view
    }

    private fun configureButtonConnect(view: View) {
        val buttonConnectWifi: Button = view.findViewById(R.id.buttonSpotifyConnect)
        buttonConnectWifi.setOnClickListener {

        }
    }
}
