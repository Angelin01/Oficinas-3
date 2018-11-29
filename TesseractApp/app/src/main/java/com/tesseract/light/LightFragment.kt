package com.tesseract.light

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tesseract.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class LightFragment : Fragment() {

	private lateinit var lightController: LightController

	private lateinit var colorImageViews: ArrayList<ImageView>
	private lateinit var colorTextViews: ArrayList<TextView>
	private lateinit var textViewsParameterNames: ArrayList<TextView>
	private lateinit var textViewsParameterValues: ArrayList<TextView>

	lateinit var spinner: Spinner
	lateinit var faceSpinner: Spinner

	private fun loadResourcesFromMemory() {
		val userPatterns = readFromMemory()
		if (!userPatterns.isEmpty()) {
			lightController.addUserPatternsFromString(userPatterns)
		}

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_light, container, false)

		lightController = activity?.run { ViewModelProviders.of(this).get(LightController::class.java) }!!

		loadResourcesFromMemory()
		this.initializeColorViews(view)

		setupFaceSpinner(view)
		setupPatternSpinner(view)
		setupEditPatternsButton(view, savedInstanceState)
		setupFinishSelectButton(view)
		setupDeletePatternButton(view)

		return view
	}

	private fun setupFinishSelectButton(view: View) {
		val imageViewEditPatters: Button = view.findViewById(R.id.buttonFinishLightSelection)
		imageViewEditPatters.setOnClickListener {
			lightController.setConfigurationOnTesseract()
		}
	}

	private fun setupPatternSpinner(view: View) {
		spinner = view.findViewById(R.id.spinner_leds_patterns)
		val spinnerAdapter: ArrayAdapter<Light> = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, lightController.lightPatterns)
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		spinner.adapter = spinnerAdapter

		spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				val light: Light = parent!!.selectedItem as Light
				updateLightParameters(light)
				updateSelectedPattern(light, position)
			}
		}
	}

	private fun setupDeletePatternButton(view: View) {
		val imageButtonDeletePattern: ImageButton = view.findViewById(R.id.imageButtonDeletePattern)
		imageButtonDeletePattern.setOnClickListener {
			lightController.deletePattern(lightController.selectedPatterns[lightController.currentFace]!!)
			updateLightParameters(lightController.lightPatterns[0])
			updateSelectedPattern(lightController.lightPatterns[0], 0)
			writeToMemory(lightController.getListPatternsAsString().toString())
			spinner.setSelection(0)
		}
	}

	private fun updateSelectedPattern(light: Light, position: Int) {
		lightController.selectedPatterns[lightController.currentFace] = light.copy()
		lightController.selectedPatternsIndexes[lightController.currentFace] = position
	}

	private fun setupFaceSpinner(view: View) {
		faceSpinner = view.findViewById(R.id.spinner_leds_face)
		val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, lightController.lightFaces)
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		faceSpinner.adapter = spinnerAdapter

		faceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				lightController.currentFace = position
				updateLightParameters(lightController.selectedPatterns[lightController.currentFace]!!)
				spinner.setSelection(lightController.selectedPatternsIndexes[position])
			}
		}
	}


	private fun setupEditPatternsButton(view: View, savedInstanceState: Bundle?) {
		val imageViewEditPatters: ImageButton = view.findViewById(R.id.imageButtonEditPatterns)
		imageViewEditPatters.setOnClickListener {
			val transaction = fragmentManager!!.beginTransaction()
			transaction.replace(R.id.home_view_frame, LightCreateFragment())
			if (savedInstanceState == null) {
				transaction.addToBackStack(null)
			}
			transaction.commit()
		}
	}

	private fun initializeColorViews(view: View) {
		colorImageViews = ArrayList()
		with(colorImageViews) {
			add(view.findViewById(R.id.imageViewFirstColor))
			add(view.findViewById(R.id.imageViewSecondColor))
			add(view.findViewById(R.id.imageViewThirdColor))
		}
		colorTextViews = ArrayList()
		with(colorTextViews) {
			add(view.findViewById(R.id.textViewFirstColor))
			add(view.findViewById(R.id.textViewSecondColor))
			add(view.findViewById(R.id.textViewThirdColor))
		}

		textViewsParameterNames = ArrayList()
		with(textViewsParameterNames) {
			add(view.findViewById(R.id.textViewFirstParameterName))
			add(view.findViewById(R.id.textViewSecondParameterName))
			add(view.findViewById(R.id.textViewThirdParameterName))
		}

		textViewsParameterValues = ArrayList()
		with(textViewsParameterValues) {
			add(view.findViewById(R.id.textViewFirstParameterValue))
			add(view.findViewById(R.id.textViewSecondParameterValue))
			add(view.findViewById(R.id.textViewThirdParameterValue))
		}
	}

	private fun updateLightParameters(light: Light) {
		for (colorImageView: ImageView in this.colorImageViews) {
			colorImageView.visibility = View.INVISIBLE
		}
		setTextViewInvisible(colorTextViews)

		setTextViewInvisible(textViewsParameterNames)
		setTextViewInvisible(textViewsParameterValues)

		val textViewDescription: TextView = view!!.findViewById(R.id.textViewDescription)
		textViewDescription.text = light.description

		val textViewPatternName: TextView = view!!.findViewById(R.id.textViewPatternName)
		textViewPatternName.text = light.pattern

		light.colors_parameters.forEachIndexed { index, element ->
			this.colorTextViews[index].visibility = View.VISIBLE
			this.colorTextViews[index].text = element
		}

		light.colors.forEachIndexed { index, element ->
			this.colorImageViews[index].visibility = View.VISIBLE
			this.colorImageViews[index].setBackgroundColor(Color.parseColor(element))
		}

		trySetParameters(light)
	}

	private fun trySetParameters(light: Light) {
		Log.d("TAG", light.toString())

		var parameterIndex = 0
		if (light.intensity != null) {
			textViewsParameterNames[parameterIndex].text= "Intensity"
			textViewsParameterNames[parameterIndex].visibility = View.VISIBLE
			textViewsParameterValues[parameterIndex].text = light.intensity.toString()
			textViewsParameterValues[parameterIndex].visibility = View.VISIBLE
			parameterIndex += 1
		}

		if (light.speed != null) {
			textViewsParameterNames[parameterIndex].text= "Speed"
			textViewsParameterNames[parameterIndex].visibility = View.VISIBLE
			textViewsParameterValues[parameterIndex].text = light.speed.toString()
			textViewsParameterValues[parameterIndex].visibility = View.VISIBLE
			parameterIndex += 1
		}

		if (light.modifier != null) {
			textViewsParameterNames[parameterIndex].text= "Modifier"
			textViewsParameterNames[parameterIndex].visibility = View.VISIBLE
			textViewsParameterValues[parameterIndex].text = light.modifier
			textViewsParameterValues[parameterIndex].visibility = View.VISIBLE
			parameterIndex += 1
		}
	}

	private fun setTextViewInvisible(colorTextViews1: ArrayList<TextView>) {
		for (colorTextView: TextView in colorTextViews1) {
			colorTextView.visibility = View.INVISIBLE
		}
	}


	private fun readFromMemory(): String {
		val path = context!!.filesDir
		val file = File(path, lightController.filename)
		if (!file.exists()) {
			file.createNewFile()
		}

		val length = file.length().toInt()
		val bytes = ByteArray(length)

		val inputStream = FileInputStream(file)
		try {
			inputStream.read(bytes)
		} finally {
			inputStream.close()
		}

		return String(bytes)
	}

	private fun writeToMemory(string: String) {
		val path = context!!.filesDir
		val file = File(path, lightController.filename)
		if (!file.exists()) {
			file.createNewFile()
		}

		val stream = FileOutputStream(file)
		try {
			stream.write(string.toByteArray())
		} finally {
			stream.close()
		}
	}

}
