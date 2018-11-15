package com.tesseract.spotify

import java.io.Serializable

data class SpotifyConnectionValues(val token: String, val deviceID: String) : Serializable
