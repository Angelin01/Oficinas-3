package com.tesseract.wifi

import java.io.Serializable

data class Wifi(val ssid: String, val signal: Int?, val encryption_type: String?, var psk: String?) : Serializable
