package com.tesseract.communication

import android.util.Log
import com.google.gson.Gson
import com.tesseract.wifi.Wifi

object WifiCommunication {
	private val sampleWifiConnect: String = """
		{
   "type":"wifi",
   "subtype":"connect",
   "value":{
      "ssid":"Boberg",
      "psk":"minhapskfeliz"
   }
}
"""

	private val temp:  String = """{"subtype":"connect","type":"wifi","value":"{\"password\":\"teste\",\"ssid\":\"Boberg\"}"}"""

	private val sampleWifiList: String = """[
    {
      "signal":-45,
      "ssid":"OURHOMEWIFI",
      "encryption_type":"wpa2"
    },
    {
      "signal":-90,
      "ssid":"Boberg",
      "encryption_type":"wpa2"
    }
]"""

	fun requestAvailableWifi(): List<Wifi> {
		this.requestWifiList()
		val gson = Gson()
		val wifiList: List<Wifi> = gson.fromJson(sampleWifiList, Array<Wifi>::class.java).toList()
		return wifiList
	}

	private fun requestWifiList() {
		TesseractCommunication.sendRequest("wifi", "request-list", "null")
	}

	fun connectToWifi(wifi: Wifi) {
		val gson = Gson()
		val wifiJson: String = gson.toJson(wifi)

		Log.d("TAG", wifiJson)
		TesseractCommunication.sendRequest("wifi", "connect", wifiJson)
	}

	fun getAvailableWifi(values: ArrayList<String>): List<Wifi> {
		val gson = Gson()
		val wifiList: ArrayList<Wifi> = ArrayList()
		for (wifi_available: Any in values) {
			wifiList.add(gson.fromJson(gson.toJson(wifi_available), Wifi::class.java))
		}
		return wifiList
	}

}