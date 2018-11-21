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
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.tesseract.R
import kotlinx.android.synthetic.main.fragment_light_create.*
import java.io.File
import java.io.FileOutputStream

class LightCreateFragment : Fragment() {

	private lateinit var selectingColor: ImageView
	private var selectingColorIndex: Int = 0
	private lateinit var editingLight: Light

	private lateinit var lightController: LightController

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_light_create, container, false)

		lightController = activity?.run { ViewModelProviders.of(this).get(LightController::class.java) }!!

		this.initializeColorViews(view)

		val lights: ArrayList<Light> = this.getLights()

		initializeSpinner(view, lights)
		initializeColorPicker(view, lights.get(0))
		defineSelectColorListeners(view)

		val finishButton: Button = view.findViewById(R.id.buttonFinishLightEdit)
		finishButton.setOnClickListener {
			editingLight.name = editTextLightName.text.toString()
			lightController.sendNewLightPattern(editingLight)
			saveNewPattern()
			val transaction = fragmentManager!!.beginTransaction()
			transaction.replace(R.id.home_view_frame, LightFragment())
			transaction.addToBackStack(null)
			transaction.commit()
		}

		return view
	}

	private fun defineSelectColorListeners(view: View) {
		val colorFirstImageView: ImageView = view.findViewById(R.id.editImageViewFirstColor)
		colorFirstImageView.setOnClickListener {
			Log.d("TAG", "First image clicked")
			selectingColor.setImageResource(0)
			selectingColor = colorFirstImageView
			selectingColor.setImageResource(R.drawable.highlight_lights)
			selectingColorIndex = 0
		}

		val colorSecondImageView: ImageView = view.findViewById(R.id.editImageViewSecondColor)
		colorSecondImageView.setOnClickListener {
			Log.d("TAG", "Second image clicked")
			selectingColor.setImageResource(0)
			selectingColor = colorSecondImageView
			selectingColor.setImageResource(R.drawable.highlight_lights)
			selectingColorIndex = 1
		}

		val colorThirdImageView: ImageView = view.findViewById(R.id.editImageViewThirdColor)
		colorThirdImageView.setOnClickListener {
			Log.d("TAG", "Third image clicked")
			selectingColor.setImageResource(0)
			selectingColor = colorThirdImageView
			selectingColor.setImageResource(R.drawable.highlight_lights)
			selectingColorIndex = 2
		}

		val colorFourthImageView: ImageView = view.findViewById(R.id.editImageViewFourthColor)
		colorFourthImageView.setOnClickListener {
			Log.d("TAG", "Fourth image clicked")
			selectingColor.setImageResource(0)
			selectingColor = colorFourthImageView
			selectingColor.setImageResource(R.drawable.highlight_lights)
			selectingColorIndex = 3
		}
	}
	private lateinit var colorPickerView: ColorPickerView

	private fun initializeColorPicker(view: View, light: Light) {
		selectingColor = view.findViewById(R.id.editImageViewFirstColor)
		selectingColor.setImageResource(R.drawable.highlight_lights)
		colorPickerView = view.findViewById(R.id.colorPickerView)
		colorPickerView.setColorListener(ColorEnvelopeListener{ colorEnvelope, _ ->
			setColor(colorEnvelope)
		})
		editingLight = light
	}

	private fun setColor(color: ColorEnvelope) {
		selectingColor.setBackgroundColor(color.color)
		var rgbColor = color.hexCode.substring(2)

		editingLight.colors[selectingColorIndex] = "#" + rgbColor

	}

	private fun initializeSpinner(view: View, lights: ArrayList<Light>) {
		val spinner: Spinner = view.findViewById(R.id.spinner_leds_edit_patterns)
		val spinnerAdapter: ArrayAdapter<Light> = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, lights)
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

		spinner.adapter = spinnerAdapter
		spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				editingLight = parent!!.selectedItem as Light
				updateLightParameters(editingLight)
				resetSelectedColor()
			}
		}
	}

	private fun resetSelectedColor() {
		selectingColor.setImageResource(0)
		selectingColor = view!!.findViewById(R.id.editImageViewFirstColor)
		selectingColor.setImageResource(R.drawable.highlight_lights)
	}

	// TODO: replace for RecyclerView
	private lateinit var colorImageViews: ArrayList<ImageView>

	private lateinit var colorTextViews: ArrayList<TextView>
	private fun initializeColorViews(view: View) {
		colorImageViews = ArrayList()
		colorImageViews.add(view.findViewById(R.id.editImageViewFirstColor))
		colorImageViews.add(view.findViewById(R.id.editImageViewSecondColor))
		colorImageViews.add(view.findViewById(R.id.editImageViewThirdColor))
		colorImageViews.add(view.findViewById(R.id.editImageViewFourthColor))
		colorTextViews = ArrayList()
		colorTextViews.add(view.findViewById(R.id.editTextViewFirstColor))
		colorTextViews.add(view.findViewById(R.id.editTextViewSecondColor))
		colorTextViews.add(view.findViewById(R.id.editTextViewThirdColor))
		colorTextViews.add(view.findViewById(R.id.editTextViewFourthColor))
	}
	private fun updateLightParameters(light: Light) {
		for (colorImageView: ImageView in this.colorImageViews) {
			colorImageView.visibility = View.INVISIBLE
		}

		for (colorTextView: TextView in this.colorTextViews) {
			colorTextView.visibility = View.INVISIBLE
		}

		light.colors_parameters.forEachIndexed { index, element ->
			this.colorTextViews[index].visibility = View.VISIBLE
			this.colorTextViews[index].text = element
		}

		light.colors.forEachIndexed { index, element ->
			this.colorImageViews[index].visibility = View.VISIBLE
			this.colorImageViews[index].setBackgroundColor(Color.parseColor(element))
		}
	}

	private fun getLights(): ArrayList<Light> {
		val lights: ArrayList<Light> = ArrayList()
		lights.add(lightController.getLight(0))
		lights.add(lightController.getLight(1))
		return lights
	}

	private fun saveNewPattern() {
		writeToMemory(lightController.getListPatternsAsString().toString())
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
