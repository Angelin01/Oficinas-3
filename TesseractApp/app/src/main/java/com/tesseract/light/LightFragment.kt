package com.tesseract.light

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tesseract.R

class LightFragment : Fragment() {


	private lateinit var lightController: LightController

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_light, container, false)

		lightController = activity?.run { ViewModelProviders.of(this).get(LightController::class.java) }!!


		this.initializeColorViews(view)

		val lights: ArrayList<Light> = this.getLights()
		val spinner: Spinner = view.findViewById(R.id.spinner_leds_patterns)
		val spinnerAdapter: ArrayAdapter<Light> = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, lights)
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		spinner.adapter = spinnerAdapter

		spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				val light: Light = parent!!.selectedItem as Light
				updateLightParameters(light)
			}
		}


		val imageViewEditPatters: ImageButton = view.findViewById(R.id.imageButtonEditPatterns)
		imageViewEditPatters.setOnClickListener {
			val transaction = fragmentManager!!.beginTransaction()
			transaction.replace(R.id.home_view_frame, LightCreateFragment())
			transaction.addToBackStack(null)
			transaction.commit()
		}

		return view
	}

	private lateinit var colorImageViews: ArrayList<ImageView>
	private lateinit var colorTextViews: ArrayList<TextView>
	private fun initializeColorViews(view: View) {
		colorImageViews = ArrayList()
		colorImageViews.add(view.findViewById(R.id.imageViewFirstColor))
		colorImageViews.add(view.findViewById(R.id.imageViewSecondColor))
		colorImageViews.add(view.findViewById(R.id.imageViewThirdColor))
		colorTextViews = ArrayList()
		colorTextViews.add(view.findViewById(R.id.textViewFirstColor))
		colorTextViews.add(view.findViewById(R.id.textViewSecondColor))
		colorTextViews.add(view.findViewById(R.id.textViewThirdColor))
	}


	private fun updateLightParameters(light: Light) {
		for (colorImageView: ImageView in this.colorImageViews) {
			colorImageView.visibility = View.INVISIBLE
		}

		for (colorTextView: TextView in this.colorTextViews) {
			colorTextView.visibility = View.INVISIBLE
		}

		val textViewDescription: TextView = view!!.findViewById(R.id.textViewDescription)
		textViewDescription.text = light.description

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

}
