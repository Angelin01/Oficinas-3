package com.tesseract.wifi

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothMessageCallback
import com.tesseract.communication.TesseractCommunication

class WifiController : ViewModel(), BluetoothMessageCallback {
	private val REQUEST_SUBTYPE_LIST_WIFI = "list"
	private val REQUEST_TYPE_WIFI = "wifi"
	private val REQUEST_SUBTYPE_REQUEST_LIST_WIFI = "request-list"
	private val REQUEST_SUBTYPE_CONNECT = "connect"
	private val REQUEST_SUBTYPE_CONNECTION = "connection"

	override fun callbackMessageReceiver(values: Any, subtype: String?) {
		Gson()
		when (subtype) {
			REQUEST_SUBTYPE_LIST_WIFI -> {
				this.wifiList.postValue(getAvailableWifi(values as ArrayList<String>))
			}
			REQUEST_SUBTYPE_CONNECTION -> {
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
		TesseractCommunication.sendRequest(REQUEST_TYPE_WIFI, REQUEST_SUBTYPE_CONNECT, wifiJson)
	}

	fun requestAvailableWifi() {
		this.requestWifiList()
	}


	private fun requestWifiList() {
		TesseractCommunication.sendRequest(REQUEST_TYPE_WIFI, REQUEST_SUBTYPE_REQUEST_LIST_WIFI, "null")
	}


	private fun getAvailableWifi(values: ArrayList<String>): List<Wifi> {
		val gson = Gson()
		val wifiList: ArrayList<Wifi> = ArrayList()
		for (wifi_available: Any in values) {
			val wifiElement = gson.fromJson(gson.toJson(wifi_available), Wifi::class.java)
			if (wifiElementAlreadyListed(wifiList, wifiElement)) {
				continue
			}

			wifiList.add(wifiElement)
		}
		return wifiList
	}

	private fun wifiElementAlreadyListed(wifiList: ArrayList<Wifi>, wifiElement: Wifi) = wifiList.any { wifi -> wifi.ssid == wifiElement.ssid }

}