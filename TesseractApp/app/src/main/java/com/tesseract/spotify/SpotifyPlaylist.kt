package com.tesseract.spotify

import java.io.Serializable

data class SpotifyPlaylist(val name: String, val playlist_cover_URI: String, val music_quantity: Int) : Serializable

