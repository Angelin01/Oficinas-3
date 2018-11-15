package com.tesseract.wifi

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothMessageCallback
import com.tesseract.communication.TesseractCommunication

class WifiController : ViewModel(), BluetoothMessageCallback {
	override fun callbackMessageReceiver(values: Any, subtype: String?) {
		val gson = Gson()
		when (subtype) {
			"list" -> {
				this.wifiList.postValue(getAvailableWifi(values as ArrayList<String>))
			}

		}
	}

	var wifiList: MutableLiveData<List<Wifi>> = MutableLiveData()

	init {
		wifiList.value = ArrayList()
		TesseractCommunication.wifiListener = this
	}


	fun connectToWifi(wifi: Wifi) {
		val gson = Gson()
		val wifiJsonLog: String = gson.toJson(wifi)
		Log.d("TAG", wifiJsonLog)

		val wifiJson = gson.toJsonTree(wifi)
		TesseractCommunication.sendRequest("wifi", "connect", wifiJson)
	}

	fun requestAvailableWifi() {
		wifiList.value = availableWifi()
	}


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

	private fun availableWifi(): List<Wifi> {
		this.requestWifiList()
		val gson = Gson()
		val wifiList: List<Wifi> = gson.fromJson(sampleWifiList, Array<Wifi>::class.java).toList()
		return wifiList
	}

	private fun requestWifiList() {
		TesseractCommunication.sendRequest("wifi", "request-list", "null")
	}


	private fun getAvailableWifi(values: ArrayList<String>): List<Wifi> {
		val gson = Gson()
		val wifiList: ArrayList<Wifi> = ArrayList()
		for (wifi_available: Any in values) {
			wifiList.add(gson.fromJson(gson.toJson(wifi_available), Wifi::class.java))
		}
		return wifiList
	}

}