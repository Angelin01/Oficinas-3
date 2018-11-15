package com.tesseract.spotify

import android.os.AsyncTask
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SpotifyHTTPRequests {

    companion object {

        class DeviceIDRequest : AsyncTask<String, Void, String>() {
            private var tesseractDeviceID: String = ""

            override fun doInBackground(vararg strings: String): String? {
                try
                {
                    var urlConnection = URL(strings[0]).openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", "Bearer " + SpotifyController.token)
                    urlConnection.setRequestProperty("Accept", "application/json")
                    urlConnection.setRequestProperty("Content-Type", "application/json")

                    val responseCode = urlConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {
                        val serverResponse = urlConnection.inputStream.bufferedReader().use(BufferedReader::readText).toString()
                        val serverResponseJsonObject = JsonParser().parse(serverResponse).asJsonObject
                        val devicesJsonArray = serverResponseJsonObject.getAsJsonArray("devices")

                        for (element in devicesJsonArray)
                        {
                            val deviceJson = element.asJsonObject
                            if (deviceJson.get("name").asString == SpotifyController.deviceName)
                                tesseractDeviceID = deviceJson.get("id").asString
                        }
                    }
                    else
                    {
                        val error = urlConnection.errorStream.bufferedReader().use(BufferedReader::readText)
                        throw Exception(error)
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }

                return tesseractDeviceID
            }
        }

        class PlaylistNavigationRequest : AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg strings: String): String? {
                try
                {
                    var urlConnection = URL(strings[0]).openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", "Bearer " + SpotifyController.token)
                    urlConnection.setRequestProperty("Accept", "application/json")
                    urlConnection.setRequestProperty("Content-Type", "application/json")
                    urlConnection.doOutput = true

                    val writer = BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8"))
                    writer.write("device_id=" + SpotifyController.deviceID)

                    urlConnection.requestMethod = "POST"
                    val responseCode = urlConnection.responseCode
                    if (responseCode != HttpURLConnection.HTTP_OK)
                    {
                        val error = urlConnection.errorStream.bufferedReader().use(BufferedReader::readText)
                        throw Exception(error)
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }

                return null
            }
        }

        fun getDeviceID(): String
        {
            return DeviceIDRequest().execute("https://api.spotify.com/v1/me/player/devices").get()
        }

        fun postPlaylistNavigationCommand(command: String)
        {
            PlaylistNavigationRequest().execute("https://api.spotify.com/v1/me/player/" + command)
        }
    }
}
