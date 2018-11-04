package com.tesseract.wifi


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.tesseract.R


class WifiConnect : Fragment() {

	private lateinit var wifiController: WifiController

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_wifi_connect, container, false)

		wifiController = activity?.run { ViewModelProviders.of(this).get(WifiController::class.java) }!!

		val arguments = arguments
		val wifi: Wifi = arguments!!.getSerializable("wifi") as Wifi

		val textViewWifiName: TextView = view.findViewById(R.id.textViewWifiName)
		textViewWifiName.text = wifi.ssid

		val editTextWifiPassword: EditText = view.findViewById(R.id.editTextWifiPassword)

		val buttonWifiConnect: Button = view.findViewById(R.id.buttonWifiConnect)
		buttonWifiConnect.setOnClickListener {
			Log.d("TAG", "Password: ${editTextWifiPassword.text}")
			val wifiConnect: Wifi = Wifi(wifi.ssid, null, null, editTextWifiPassword.text.toString())
			wifiController.connectToWifi(wifiConnect)
		}
		return view
	}


}
