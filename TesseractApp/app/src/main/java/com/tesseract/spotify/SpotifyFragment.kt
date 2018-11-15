package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tesseract.R
import com.tesseract.SpotifyList

class SpotifyFragment : Fragment() {


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_spotify, container, false)

		// TODO: Check if spotify is connected, if it is call SpotifyList.

		val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
		transaction.replace(R.id.home_view_frame, SpotifyList())
		transaction.addToBackStack(null)
		transaction.commit()

		//val buttonSpotifyConnect: Button = view.findViewById(R.id.buttonSpotifyConnect)
		//buttonSpotifyConnect.setOnClickListener { SpotifyController.requestSpotifyToken(this.activity) }

		return view
	}

}
