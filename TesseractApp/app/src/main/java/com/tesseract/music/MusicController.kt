package com.tesseract.music

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothMessageCallback
import com.tesseract.communication.TesseractCommunication


class MusicController : ViewModel(), BluetoothMessageCallback {

	val sampleMusic: String = """[
    {
      "name": "music 1",
      "band_name": "band 1",
      "album_cover_url": "http://animallemundopet.com.br/wp-content/uploads/2014/10/Los-gatos-nos-ignoran-1-777x518.jpg",
      "duration": "1.0",
      "volume": "30"
    },
    {
      "name": "music 2",
      "band_name": "band 2",
      "album_cover_url": "https://i.ytimg.com/vi/_43lSXa1yDs/maxresdefault.jpg",
      "duration": "2.0",
      "volume": "80"
    },
    {
      "name": "music 3",
      "band_name": "band 3",
      "album_cover_url": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTiEzGp5Ww0avJTR2SiwaXEmE7vJQ__e-vaq-D4Yz4p1mN96_7SXQ",
      "duration": "3.0",
      "volume": "50"
    }
]"""

	override fun callbackMessageReceiver(values: Any, subtype: String?) {
		val gson = Gson()
		when (subtype) {
			"music" -> {
				music.postValue(gson.fromJson(gson.toJson(values), Music::class.java))
				player.postValue(player.value!!.copy(position = 0))
			}

		}

	}

	var player: MutableLiveData<Player> = MutableLiveData()
	var music: MutableLiveData<Music> = MutableLiveData()

	var musicIndex: Int = 0 // para testes

	init {
		player.value = Player()
		this.loadLastMusic()

		TesseractCommunication.musicListener = this

	}

	fun play() {
		player.value = player.value!!.copy(playing = true)
		TesseractCommunication.sendCommand("play")
		this.music.value = getMusic(musicIndex)
	}

	private fun pause() {
		player.value = player.value!!.copy(playing = false)
		TesseractCommunication.sendCommand("pause")
	}

	fun playToggle() {
		if (player.value!!.playing) {
			this.pause()
			return
		}

		this.play()
	}

	fun next() {
		musicIndex++
		if (musicIndex > 2) musicIndex = 0
		TesseractCommunication.sendCommand("next")
	}

	fun previous() {
		musicIndex--
		if (musicIndex < 0) musicIndex = 2
		TesseractCommunication.sendCommand("previous")
	}

	fun shuffleToggle() {
		if (player.value!!.shuffle) {
			player.value = player.value!!.copy(shuffle = false)
			TesseractCommunication.sendCommand("shuffle")
			return
		}

		TesseractCommunication.sendCommand("shuffle")
		player.value = player.value!!.copy(shuffle = true)
	}

	fun volume(volume: Int) {
		player.value!!.volume = volume

		if (volume < 0) {
			player.value!!.volume = 0
		} else if (volume > 100) {
			player.value!!.volume = 100
		}

		TesseractCommunication.sendCommand("volume: " + player.value!!.volume.toString())

	}

	private fun loadLastMusic() {
		if (this.music.value != null) {
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
		this.music.value = gson.fromJson(lastMusic, Music::class.java)
	}

	private fun getMusic(index: Int): Music {
		val gson = Gson()
		val music: List<Music>? = gson.fromJson(sampleMusic, Array<Music>::class.java).toList()
		return music!![index]
	}

}
