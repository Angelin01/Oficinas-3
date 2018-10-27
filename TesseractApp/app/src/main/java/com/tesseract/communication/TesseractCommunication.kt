package com.tesseract.communication

import android.util.Log
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothController

object TesseractCommunication {

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
}
