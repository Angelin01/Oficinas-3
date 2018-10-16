package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener


class LightFragment : Fragment() {

    private val patterns: List<String> = listOf("Option 1", "Option 2", "Option 3")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_light, container, false)

        val spinner: Spinner = view.findViewById(R.id.spinner_leds_patterns)
        val spinnerAdapter: ArrayAdapter<CharSequence> = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, patterns)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        val colorSelected1: RelativeLayout = view.findViewById(R.id.colorSelected1)
        val colorPickerView: ColorPickerView = view.findViewById(R.id.colorPickerView)
        colorPickerView.setColorListener(ColorListener { color, fromUser ->
            colorSelected1.setBackgroundColor(color)

        })
        return view
    }

}
