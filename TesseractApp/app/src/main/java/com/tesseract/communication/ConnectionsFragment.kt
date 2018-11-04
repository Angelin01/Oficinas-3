package com.tesseract.communication


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.tesseract.R
import com.tesseract.wifi.WifiFragment
import com.tesseract.bluetooth.BluetoothDeviceList


class ConnectionsFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val view: View = inflater.inflate(R.layout.fragment_connections, container, false)

		val buttonConnectBluetooth: RelativeLayout = view.findViewById(R.id.buttonConnectTesseract)
		buttonConnectBluetooth.setOnClickListener {
			changeToFragment(R.id.home_view_frame, BluetoothDeviceList())
		}

		val buttonConnectWifi: RelativeLayout = view.findViewById(R.id.buttonConnectTesseractWifi)
		buttonConnectWifi.setOnClickListener {
			changeToFragment(R.id.home_view_frame, WifiFragment())
		}

		return view
	}

	private fun changeToFragment(frame_layout_id: Int, fragment: Fragment) {
		val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
		transaction.replace(frame_layout_id, fragment)
		transaction.addToBackStack(null)
		transaction.commit()
	}

}
