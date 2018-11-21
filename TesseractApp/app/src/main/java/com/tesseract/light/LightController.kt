package com.tesseract.light

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.tesseract.communication.TesseractCommunication
import com.google.gson.reflect.TypeToken




class LightController() : ViewModel() {

	val filename: String = "lights_configs.json"

	private val REQUEST_TYPE: String = "light"
	private val REQUEST_SUBTYPE_SET_CONFIGURATION: String = "set-configuration"


	private val REQUEST_SUBTYPE_NEW_PATTERN: String = "new-pattern"
	private val FRONT_FACE = "Front"
	private val LEFT_FACE = "Left"
	private val RIGHT_FACE = "Right"
	private val BACK_FACE = "Back"
	private val FRONT_FACE_INDEX = 0
	private val LEFT_FACE_INDEX = 1

	private val RIGHT_FACE_INDEX = 2
	private val BACK_FACE_INDEX = 3
	val lightPatterns: ArrayList<Light>
	val lightFaces: ArrayList<String> = arrayListOf(FRONT_FACE, LEFT_FACE, RIGHT_FACE, BACK_FACE)

	val selectedPatterns: HashMap<Int, Light> = HashMap()

	var currentFace: Int = 0

	val gson = Gson()

	init {
		lightPatterns = loadDefaultPatterns()!!
		initSelectedPatterns()
	}

	fun addUserPatternsFromString(patterns: String) {
		val lights: List<Light> = gson.fromJson(patterns, Array<Light>::class.java).toList()
		for (light: Light in lights) {
			if (!lightPatterns.contains(light)) {
				lightPatterns.add(light)
			}
		}
	}

	private fun addUserPattern(pattern: Light) {
		lightPatterns.add(pattern)
	}

	fun getListPatternsAsString(): JsonElement? {
		val listType = object : TypeToken<List<Light>>() {
		}.type
		val result =  gson.toJsonTree(lightPatterns, listType)
		return  result
	}


	private fun initSelectedPatterns() {
		selectedPatterns[FRONT_FACE_INDEX] = lightPatterns[0]
		selectedPatterns[LEFT_FACE_INDEX] = lightPatterns[0]
		selectedPatterns[RIGHT_FACE_INDEX] = lightPatterns[0]
		selectedPatterns[BACK_FACE_INDEX] = lightPatterns[0]
	}

	private fun loadDefaultPatterns(): ArrayList<Light>? {
		val light: List<Light> = gson.fromJson(sampleLights, Array<Light>::class.java).toList()
		return light as ArrayList<Light>
	}

	fun getLight(index: Int): Light {
		val gson = Gson()
		val light: List<Light>? = gson.fromJson(sampleLights, Array<Light>::class.java).toList()
		return light!![index]
	}

	fun sendNewLightPattern(editingLight: Light) {
		addUserPattern(editingLight)
		val lightJson: JsonElement = gson.toJsonTree(editingLight)
		Log.d("TAG", lightJson.toString())
		TesseractCommunication.sendRequest(REQUEST_TYPE, REQUEST_SUBTYPE_NEW_PATTERN, lightJson)
	}

	fun setConfigurationOnTesseract() {
		val lightJson: JsonElement = gson.toJsonTree(selectedPatterns)
		Log.d("TAG", lightJson.toString())
		TesseractCommunication.sendRequest(REQUEST_TYPE, REQUEST_SUBTYPE_SET_CONFIGURATION, lightJson)
	}

	companion object {
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
	}
}
