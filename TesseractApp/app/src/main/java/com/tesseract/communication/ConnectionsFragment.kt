package com.tesseract.communication


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.tesseract.R
import com.tesseract.bluetooth.BluetoothStatusChangeCallback
import com.tesseract.bluetooth.BluetoothController
import com.tesseract.wifi.WifiFragment
import com.tesseract.bluetooth.BluetoothDeviceList
import com.tesseract.bluetooth.BluetoothService
import kotlinx.android.synthetic.main.fragment_connections.*


class ConnectionsFragment : Fragment(), BluetoothStatusChangeCallback {

	override fun onStatusChange(connected: Boolean) {
		updateBluetoothStatus()
	}

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

	override fun onResume() {
		super.onResume()

		updateBluetoothStatus()
	}

	private fun changeToFragment(frame_layout_id: Int, fragment: Fragment) {
		val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
		transaction.replace(frame_layout_id, fragment)
		transaction.addToBackStack(null)
		transaction.commit()
	}

	private fun updateBluetoothStatus() {
		if (relativeConnectionBluetoothStatus == null) {
			return
		}

		if (BluetoothController.bluetoothService!!.mState == BluetoothService.BluetoothStates.STATE_CONNECTED) {
			relativeConnectionBluetoothStatus.setBackgroundColor(context!!.getColor(R.color.secondaryColor))
			textViewBluetoothStatus.text = "Tesseract Connected"
		} else {
			relativeConnectionBluetoothStatus.setBackgroundColor(context!!.getColor(R.color.colorAccent))
			textViewBluetoothStatus.text = "Tesseract Disconnected"
		}
	}

}
