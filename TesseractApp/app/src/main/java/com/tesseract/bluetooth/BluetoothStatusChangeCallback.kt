package com.tesseract.bluetooth

interface BluetoothStatusChangeCallback {
    fun onStatusChange(connected: Boolean)

}