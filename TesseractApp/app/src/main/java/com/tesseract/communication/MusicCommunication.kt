package com.tesseract.communication

import com.google.gson.Gson
import com.tesseract.music.Music

object MusicCommunication {

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

	fun play() {
		TesseractCommunication.sendCommand("play")
		return
	}

	fun pause() {
		TesseractCommunication.sendCommand("pause")
		return
	}

	fun next() {
		TesseractCommunication.sendCommand("next")
		return
	}

	fun previous() {
		TesseractCommunication.sendCommand("previous")
		return
	}

	fun shuffle(enabled: Boolean) {
		TesseractCommunication.sendCommand("shuffle")
		return
	}

	fun volume(volume: Int) {
		TesseractCommunication.sendCommand("volume: " + volume.toString())
		return
	}

	//index parameter is only for tests
	fun getMusic(index: Int): Music {
		val gson = Gson()
		val music: List<Music>? = gson.fromJson(sampleMusic, Array<Music>::class.java).toList()
		return music!![index]
	}
}