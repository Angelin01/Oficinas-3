package com.tesseract.communication


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tesseract.R
import com.tesseract.bluetooth.BluetoothController
import com.tesseract.bluetooth.BluetoothDeviceList
import com.tesseract.bluetooth.BluetoothService
import com.tesseract.bluetooth.BluetoothStatusChangeCallback
import com.tesseract.wifi.WifiController
import com.tesseract.wifi.WifiFragment
import com.tesseract.wifi.WifiStatusChangeCallback
import kotlinx.android.synthetic.main.fragment_connections.*


class ConnectionsFragment : Fragment(), BluetoothStatusChangeCallback, WifiStatusChangeCallback {

	override fun onWifiStatusChange(connected: Boolean, ssid: String?) {
		updateWifiStatus()
	}

	override fun onStatusChange(connected: Boolean) {
		updateBluetoothStatus()
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val view: View = inflater.inflate(R.layout.fragment_connections, container, false)

		var wifiController = activity?.run { ViewModelProviders.of(this).get(WifiController::class.java) }!!
		wifiController.wifiCallback = this

		val buttonConnectBluetooth: ConstraintLayout = view.findViewById(R.id.buttonConnectTesseract)
		buttonConnectBluetooth.setOnClickListener {
			changeToFragment(R.id.home_view_frame, BluetoothDeviceList())
		}

		val buttonConnectWifi: ConstraintLayout = view.findViewById(R.id.buttonConnectTesseractWifi)
		buttonConnectWifi.setOnClickListener {
			changeToFragment(R.id.home_view_frame, WifiFragment())
		}

		return view
	}

	override fun onResume() {
		super.onResume()

		updateBluetoothStatus()
		updateWifiStatus()
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

	private fun updateWifiStatus() {
		if (relativeConnectionWifiStatus == null) {
			return
		}

		if (WifiController.connected) {
			relativeConnectionWifiStatus.setBackgroundColor(context!!.getColor(R.color.secondaryColor))
			textViewWifiStatus.text = "Tesseract Connected: ${WifiController.connectedSSID}"
		} else {
			relativeConnectionWifiStatus.setBackgroundColor(context!!.getColor(R.color.colorAccent))
			textViewWifiStatus.text = "Tesseract Disconnected"
		}
	}
}
