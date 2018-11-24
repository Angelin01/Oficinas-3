package com.tesseract.light

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.tesseract.communication.TesseractCommunication
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder




class LightController() : ViewModel() {

	val filename: String = "lights_configs.json"

	private val REQUEST_TYPE: String = "light"
	private val REQUEST_SUBTYPE_SET_CONFIGURATION: String = "set-configuration"


	private val REQUEST_SUBTYPE_NEW_PATTERN: String = "new-pattern"
	private val FRONT_FACE = "front"
	private val LEFT_FACE = "left"
	private val RIGHT_FACE = "right"
	private val BACK_FACE = "back"
	private val FRONT_FACE_INDEX = 0
	private val LEFT_FACE_INDEX = 1
	private val RIGHT_FACE_INDEX = 2
	private val BACK_FACE_INDEX = 3

	val lightPatterns: ArrayList<Light>
	val lightFaces: ArrayList<String> = arrayListOf(FRONT_FACE, LEFT_FACE, RIGHT_FACE, BACK_FACE)

	val selectedPatterns: HashMap<Int, Light> = HashMap()
	val selectedPatternsIndexes: ArrayList<Int> = ArrayList()

	var currentFace: Int = 0

	private val gson: Gson = GsonBuilder().serializeNulls().create()

	init {
		lightPatterns = getPatterns()
		initSelectedPatterns()
		initSelectedPatternsIndexes()
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
		selectedPatterns[FRONT_FACE_INDEX] =  lightPatterns[0].copy()
		selectedPatterns[LEFT_FACE_INDEX] = lightPatterns[0].copy()
		selectedPatterns[RIGHT_FACE_INDEX] = lightPatterns[0].copy()
		selectedPatterns[BACK_FACE_INDEX] = lightPatterns[0].copy()
	}

	private fun initSelectedPatternsIndexes() {
		selectedPatternsIndexes.add(0)
		selectedPatternsIndexes.add(0)
		selectedPatternsIndexes.add(0)
		selectedPatternsIndexes.add(0)
	}

	fun getPatterns(): ArrayList<Light> {
		val light: List<Light> = gson.fromJson(sampleLights, Array<Light>::class.java).toList()
		return ArrayList(light)
	}

	fun sendNewLightPattern(editingLight: Light) {
		addUserPattern(editingLight)
		val lightJson: JsonElement = gson.toJsonTree(editingLight)
		Log.d("TAG", lightJson.toString())
		TesseractCommunication.sendRequest(REQUEST_TYPE, REQUEST_SUBTYPE_NEW_PATTERN, lightJson)
	}

	fun setConfigurationOnTesseract() {
		val lights = ArrayList<Light>()
		selectedPatterns[0]!!.face = "front"
		selectedPatterns[1]!!.face = "left"
		selectedPatterns[2]!!.face = "right"
		selectedPatterns[3]!!.face = "back"
		convertHashMapToList(0, "front", lights)
		convertHashMapToList(1, "left", lights)
		convertHashMapToList(2, "right", lights)
		convertHashMapToList(3, "back", lights)
		Log.d("TAG", lights.toString())


		val lightJson: JsonElement = gson.toJsonTree(lights)
		Log.d("TAG", lightJson.toString())
		TesseractCommunication.sendRequest(REQUEST_TYPE, REQUEST_SUBTYPE_SET_CONFIGURATION, lightJson)
	}

	private fun convertHashMapToList(index: Int, face_name: String, lights: ArrayList<Light>) {
		val light_front = selectedPatterns.get(index)!!
		light_front.face = face_name
		lights.add(light_front)
	}

	public fun getFaceIndexByName(name: String): Int {
		if (name == FRONT_FACE)
			return FRONT_FACE_INDEX
		if (name == LEFT_FACE)
			return LEFT_FACE_INDEX
		if (name == RIGHT_FACE)
			return RIGHT_FACE_INDEX
		if (name == BACK_FACE)
			return BACK_FACE_INDEX

		return FRONT_FACE_INDEX
	}

	companion object {
		private const val sampleLights: String = """[
		{
		  "name": "wave",
		  "description": "Fast",
		  "pattern" : "wave",
		  "colors": ["#43e1ff", "#00574B", "#D81B60"],
		  "colors_parameters": ["color 1", "color 2", "color 3"],
		  "speed": 1,
		  "intensity" : 80,
		  "face": null,
		  "modifier": "rising"
		},
		{
		  "name": "breathe",
		  "description": "",
		  "pattern" : "breathe",
		  "colors": [],
		  "colors_parameters": [],
		  "speed": 1,
		  "intensity" : 80,
		  "face": null,
		  "modifier": "none"
		},
		{
		  "name": "stream",
		  "description": "",
		  "pattern" : "stream",
		  "colors": [],
		  "colors_parameters": [],
		  "speed": 1,
		  "intensity" : 80,
		  "face": null,
		  "modifier": "none"
		},
		{
		  "name": "FFT",
		  "description": "",
		  "pattern" : "fft_color",
		  "colors": ["#43e1ff", "#00574B", "#D81B60"],
		  "colors_parameters": ["Low", "Medium", "High"],
		  "speed": 1,
		  "intensity" : 80,
		  "face": null,
		  "modifier": "none"
		},
		{
		  "name": "FFT Bars",
		  "description": "",
		  "pattern" : "fft_bars",
		  "colors": ["#43e1ff", "#00574B", "#D81B60"],
		  "colors_parameters": ["Low", "Medium", "High"],
		  "speed": 1,
		  "intensity" : 80,
		  "face": null,
		  "modifier": "none"
		}
	]"""
	}
}
