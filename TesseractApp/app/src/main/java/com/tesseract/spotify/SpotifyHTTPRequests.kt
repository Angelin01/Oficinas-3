package com.tesseract.spotify

import android.os.AsyncTask
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
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

        class PlaylistNavigationPostRequest : AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg strings: String): String? {
                try
                {
                    var urlConnection = URL(strings[0]).openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", "Bearer " + SpotifyController.token)
                    urlConnection.setRequestProperty("Accept", "application/json")
                    urlConnection.setRequestProperty("Content-Type", "application/json")
                    urlConnection.doOutput = true

                    /*val writer = BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8"))
                    writer.write("device_id=" + SpotifyController.deviceID)*/

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

        class PlaylistNavigationPutRequest : AsyncTask<String, Void, String>() {

            override fun doInBackground(vararg strings: String): String? {
                try
                {
                    var urlConnection = URL(strings[0]).openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", "Bearer " + SpotifyController.token)
                    urlConnection.doOutput = true

                    /*val writer = BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8"))
                    writer.write("device_id=" + SpotifyController.deviceID)*/

                    urlConnection.requestMethod = "PUT"
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

        class PlaybackInfoRequest : AsyncTask<String, Void, String>() {
            private var playbackInfo: String = ""

            override fun doInBackground(vararg strings: String): String? {
                try
                {
                    var urlConnection = URL(strings[0]).openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", "Bearer " + SpotifyController.token)
                    urlConnection.setRequestProperty("Accept", "application/json")
                    urlConnection.setRequestProperty("Content-Type", "application/json")

                    val responseCode = urlConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK)
                        playbackInfo = urlConnection.inputStream.bufferedReader().use(BufferedReader::readText).toString()
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

                return playbackInfo
            }
        }

        class UserPlaylistsRequest : AsyncTask<String, Void, String>() {
            private var userPlaylists: String = ""

            override fun doInBackground(vararg strings: String): String? {
                try
                {
                    var urlConnection = URL(strings[0]).openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", "Bearer " + SpotifyController.token)
                    urlConnection.setRequestProperty("Accept", "application/json")
                    urlConnection.setRequestProperty("Content-Type", "application/json")

                    val responseCode = urlConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK)
                        userPlaylists = urlConnection.inputStream.bufferedReader().use(BufferedReader::readText).toString()
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

                return userPlaylists
            }
        }

        fun getDeviceID(): String
        {
            return DeviceIDRequest().execute("https://api.spotify.com/v1/me/player/devices").get()
        }

        fun postPlaylistNavigationCommand(command: String)
        {
            PlaylistNavigationPostRequest().execute("https://api.spotify.com/v1/me/player/" + command).get()
        }

        fun putPlaylistNavigationCommand(command: String)
        {
            PlaylistNavigationPutRequest().execute("https://api.spotify.com/v1/me/player/" + command).get()
        }

        fun getPlaybackInfo(): JsonObject?
        {
            val playbackInfo = PlaybackInfoRequest().execute("https://api.spotify.com/v1/me/player").get()

            if (playbackInfo == "")
                return null

            return JsonParser().parse(playbackInfo).asJsonObject
        }

        fun getUserPlaylists(): JsonObject
        {
            val userPlaylists = UserPlaylistsRequest().execute("https://api.spotify.com/v1/me/playlists").get()
            return JsonParser().parse(userPlaylists).asJsonObject
        }
    }
}
