package com.tesseract.Music

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.tesseract.communication.TesseractCommunication


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
        TesseractCommunication.play()
        this.music = TesseractCommunication.getMusic(musicIndex)
    }

    private fun pause() {
        this.playing = false
        TesseractCommunication.pause()
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
        TesseractCommunication.next()
    }

    fun previous() {
        musicIndex--
        if (musicIndex < 0)
            musicIndex = 2
        TesseractCommunication.previous()
    }

    fun shuffleToggle() {
        if (this.shuffle) {
            this.shuffle = false
            TesseractCommunication.shuffle(false)
            return
        }

        TesseractCommunication.shuffle(true)
        this.shuffle = true
    }

    fun volume(volume: Int) {
        this.volume = volume

        if (volume < 0) {
            this.volume = 0
        } else if (volume > 100) {
            this.volume = 100
        }

        TesseractCommunication.volume(this.volume)

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
