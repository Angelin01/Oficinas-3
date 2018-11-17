package com.tesseract.light

data class Light(var name: String, val description: String, var colors: ArrayList<String>, val colors_parameters: ArrayList<String>) {

    override fun toString(): String {
        return name
    }
}
