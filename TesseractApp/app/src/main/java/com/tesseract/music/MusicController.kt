package com.tesseract.music

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.tesseract.communication.MusicCommunication


class MusicController : ViewModel() {
	private var volume: Int = 0
	var shuffle: Boolean = false
	var playing: Boolean = false

	var music: Music? = null

	var musicIndex: Int = 0 // para testes

	init {
		this.loadLastMusic()
	}

	fun play() {
		this.playing = true
		MusicCommunication.play()
		this.music = MusicCommunication.getMusic(musicIndex)
	}

	private fun pause() {
		this.playing = false
		MusicCommunication.pause()
		// sendo command to pause
	}

	fun playToggle() {
		if (this.playing) {
			this.pause()
			return
		}

		this.play()
	}

	fun next() {
		musicIndex++
		if (musicIndex > 2) musicIndex = 0
		MusicCommunication.next()
	}

	fun previous() {
		musicIndex--
		if (musicIndex < 0) musicIndex = 2
		MusicCommunication.previous()
	}

	fun shuffleToggle() {
		if (this.shuffle) {
			this.shuffle = false
			MusicCommunication.shuffle(false)
			return
		}

		MusicCommunication.shuffle(true)
		this.shuffle = true
	}

	fun volume(volume: Int) {
		this.volume = volume

		if (volume < 0) {
			this.volume = 0
		} else if (volume > 100) {
			this.volume = 100
		}

		MusicCommunication.volume(this.volume)

	}

	private fun loadLastMusic() {
		if (this.music != null) {
			Log.d("TAG", "Music already loaded")
			return
		}

		// load last music from memory
		val lastMusic: String = """{
  name: "music 2",
  band_name: "band 2",
  album_cover_url: "https://i.ytimg.com/vi/_43lSXa1yDs/maxresdefault.jpg",
  duration: 2.0
}"""

		val gson = Gson()
		this.music = gson.fromJson(lastMusic, Music::class.java)
	}

}
