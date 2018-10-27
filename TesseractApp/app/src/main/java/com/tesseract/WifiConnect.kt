package com.tesseract


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.tesseract.communication.WifiCommunication
import com.tesseract.wifi.Wifi


class WifiConnect : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_wifi_connect, container, false)

		val arguments = arguments
		val wifi: Wifi = arguments!!.getSerializable("wifi") as Wifi

		val textViewWifiName: TextView = view.findViewById(R.id.textViewWifiName)
		textViewWifiName.text = wifi.ssid

		val editTextWifiPassword: EditText = view.findViewById(R.id.editTextWifiPassword)

		val buttonWifiConnect: Button = view.findViewById(R.id.buttonWifiConnect)
		buttonWifiConnect.setOnClickListener {
			Log.d("TAG", "Password: ${editTextWifiPassword.text}")
			val wifiConnect: Wifi = Wifi(wifi.ssid, null, null, editTextWifiPassword.text.toString())
			WifiCommunication.connectToWifi(wifiConnect)
		}
		return view
	}


}
