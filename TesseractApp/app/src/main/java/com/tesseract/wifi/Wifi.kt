package com.tesseract.wifi

import java.io.Serializable

class Wifi(val ssid: String, val signal: Int?, val encryption_type: String?, var psk: String?) : Serializable
