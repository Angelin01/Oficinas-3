package com.tesseract

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson


class MusicController : ViewModel() {
    private var volume: Int = 0
    var shuffle: Boolean = false
    var playing: Boolean = false

    private val tesseralCommunication: TesseractCommunication = TesseractCommunication
    var music: Music? = null

    var musicIndex: Int = 0 // para testes

    init {
        this.loadLastMusic()
    }

    fun play() {
        this.playing = true
        tesseralCommunication.play()
        this.music = tesseralCommunication.getMusic(musicIndex)
    }

    private fun pause() {
        this.playing = false
        tesseralCommunication.pause()
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
        if (musicIndex > 2)
            musicIndex = 0
        tesseralCommunication.next()
    }

    fun previous() {
        musicIndex--
        if (musicIndex < 0)
            musicIndex = 2
        tesseralCommunication.previous()
    }

    fun shuffleToggle() {
        if (this.shuffle) {
            this.shuffle = false
            tesseralCommunication.shuffle(false)
            return
        }

        tesseralCommunication.shuffle(true)
        this.shuffle = true
    }

    fun volume(volume: Int) {
        this.volume = volume

        if (volume < 0) {
            this.volume = 0
        } else if (volume > 100) {
            this.volume = 100
        }

        tesseralCommunication.volume(this.volume)

    }

    private fun loadLastMusic() {
        if (this.music != null) {
            Log.d("TAG", "Music already loaded")
            return
        }

        // load last music from memory
        val lastMusic: String = "{\n" +
                "  name: \"music 2\",\n" +
                "  band_name: \"band 2\",\n" +
                "  album_cover_url: \"https://i.ytimg.com/vi/_43lSXa1yDs/maxresdefault.jpg\",\n" +
                "  duration: 2.0\n" +
                "}"

        val gson = Gson()
        this.music = gson.fromJson(lastMusic, Music::class.java)
    }

}
