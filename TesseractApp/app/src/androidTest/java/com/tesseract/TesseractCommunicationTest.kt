package com.tesseract

import org.junit.Test

class TesseractCommunicationTest {

    @Test
    fun getMusic() {
        val com = TesseractCommunication
        val re: Music = com.getMusic()
        print(re)
        print("Aqui")
    }
}