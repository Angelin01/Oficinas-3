package com.tesseract.communication

import android.util.Log
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothController
import com.tesseract.bluetooth.BluetoothMessageCallback

object TesseractCommunication: BluetoothMessageCallback {

	override fun callbackMessageReceiver(values: Any, subtype: String?) {
		val gson = Gson()
		Log.d("TAG", values.toString())
		val request: Request? = gson.fromJson(values as String, Request::class.java)
		if (request?.type.toString() == "music") {
			musicListener.callbackMessageReceiver(request!!.value, request.subtype)
		} else if (request?.type.toString() == "wifi") {
			wifiListener.callbackMessageReceiver(request!!.value, request.subtype)
		}
	}

	fun sendCommand(command: String) {
		val string: ByteArray = command.toByteArray()
		BluetoothController.bluetoothService!!.write(string)
	}

	fun sendRequest(type: String, subtype: String, values: Any) {
		val newRequest = Request(type, subtype, values)
		val gson = Gson()
		val request: String = gson.toJson(newRequest)
		Log.d("TAG", "Request: $request")
		TesseractCommunication.sendCommand(request)
	}

	lateinit var musicListener: BluetoothMessageCallback
	lateinit var wifiListener: BluetoothMessageCallback
}
