package com.tesseract.light

class Light(var name: String, val description: String, var colors: ArrayList<String>, val colors_parameters: ArrayList<String>) {

    override fun toString(): String {
        return name;
    }
}
