package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.tesseract.spotify.SpotifyController

class SpotifyFragment : Fragment() {


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_spotify_connect, container, false)

		chooseFragment(view)

		return view
	}

	override fun onResume() {
		super.onResume()
		chooseFragment(this.view!!)
	}

	private fun chooseFragment(view: View) {
		if (SpotifyController.isActive) {
			changeFragment(R.id.home_view_frame, SpotifyList())
		} else {
			val buttonSpotifyConnect: RelativeLayout = view.findViewById(R.id.buttonSpotifyConnect)
			buttonSpotifyConnect.setOnClickListener { SpotifyController.requestSpotifyToken(this.activity) }
		}
	}

	private fun changeFragment(home_view_frame: Int, spotifyList: SpotifyList) {
		val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
		transaction.replace(home_view_frame, spotifyList)
		transaction.commit()
	}

}
