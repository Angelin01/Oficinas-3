package com.tesseract.spotify

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.widget.Button
import android.view.View
import android.view.ViewGroup
import com.tesseract.R

class SpotifyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_spotify, container, false)
        configureButtonConnect(view)
        return view
    }

    private fun configureButtonConnect(view: View) {
        val buttonSpotifyConnect: Button = view.findViewById(R.id.buttonSpotifyConnect)
        buttonSpotifyConnect.setOnClickListener {
            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.home_view_frame, SpotifyConnect())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
