package com.tesseract.communication

import android.util.Log
import com.google.gson.Gson
import com.tesseract.light.Light

object LightCommunication {

	private const val sampleLights: String = """[
	{
	  "name": "rainbow",
	  "description": "A beauty unicorn",
	  "colors": ["#43e1ff", "#00574B", "#D81B60"],
	  "colors_parameters": ["low frequency", "medium frequency", "high frequency" ]
	},
	{
	  "name": "Shiny",
	  "description": "Quiet light",
	  "colors": ["#D81B60"],
	  "colors_parameters": ["All"]
	}
]"""

	fun getLight(index: Int): Light {
		val gson = Gson()
		val light: List<Light>? = gson.fromJson(sampleLights, Array<Light>::class.java).toList()
		return light!![index]
	}

	fun sendLightConfigurations(editingLight: Light) {
		val gson = Gson()
		val lightJson: String = gson.toJson(editingLight)

		Log.d("TAG", lightJson)
		TesseractCommunication.sendCommand(lightJson)
	}
}