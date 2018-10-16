package com.tesseract

import com.google.gson.Gson

object TesseractCommunication {

    val sampleMusics: String = "[\n" +
            "    {\n" +
            "      \"name\": \"music 1\",\n" +
            "      \"band_name\": \"band 1\",\n" +
            "      \"album_cover_url\": \"http://animallemundopet.com.br/wp-content/uploads/2014/10/Los-gatos-nos-ignoran-1-777x518.jpg\",\n" +
            "      \"duration\": \"1.0\",\n" +
            "      \"volume\": \"30\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"music 2\",\n" +
            "      \"band_name\": \"band 2\",\n" +
            "      \"album_cover_url\": \"https://i.ytimg.com/vi/_43lSXa1yDs/maxresdefault.jpg\",\n" +
            "      \"duration\": \"2.0\",\n" +
            "      \"volume\": \"80\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"music 3\",\n" +
            "      \"band_name\": \"band 3\",\n" +
            "      \"album_cover_url\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTiEzGp5Ww0avJTR2SiwaXEmE7vJQ__e-vaq-D4Yz4p1mN96_7SXQ\",\n" +
            "      \"duration\": \"3.0\",\n" +
            "      \"volume\": \"50\"\n" +
            "    }\n" +
            "]"

    fun play() {
        sendCommand("play")
        return
    }

    fun pause() {
        sendCommand("pause")
        return
    }

    fun next() {
        sendCommand("next")
        return
    }

    fun previous() {
        sendCommand("previous")
        return
    }

    fun shuffle(enabled: Boolean) {
        sendCommand("shuffle")
        return
    }

    fun volume(volume: Int) {
        sendCommand("volume: " + volume.toString())
        return
    }

    //index parameter is only for tests
    fun getMusic(index: Int): Music {
        val gson = Gson()
        val music: List<Music>? = gson.fromJson(sampleMusics, Array<Music>::class.java).toList()
        return music!![index]
    }

    private fun sendCommand(command: String) {
        val play: ByteArray = command.toByteArray()
        BluetoothController.bluetoothService!!.write(play)
    }

}
