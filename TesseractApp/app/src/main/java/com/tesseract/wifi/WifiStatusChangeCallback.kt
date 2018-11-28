package com.tesseract.wifi

interface WifiStatusChangeCallback {
	fun onWifiStatusChange(connected: Boolean, ssid: String?)
}