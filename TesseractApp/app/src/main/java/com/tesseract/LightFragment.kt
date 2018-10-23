package com.tesseract

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class LightFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_light, container, false)

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
            transaction.replace(R.id.home_view_frame, LightCreate())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun updateLightParameters(light: Light) {
        val textViewDescription: TextView = view!!.findViewById(R.id.textViewDescription)
        textViewDescription.text = light.description
        val imageViewFirstColor: ImageView = view!!.findViewById(R.id.imageViewFirstColor)
        imageViewFirstColor.setBackgroundColor(Color.parseColor(light.colors[0]))
        val textViewFirstColor: TextView = view!!.findViewById(R.id.textViewFirstColor)
        textViewFirstColor.text = light.colors_parameters[0]
    }

    private fun getLights(): ArrayList<Light> {
        val lights: ArrayList<Light> = ArrayList()
        lights.add(TesseractCommunication.getLight(0))
        lights.add(TesseractCommunication.getLight(1))
        return lights
    }

}
