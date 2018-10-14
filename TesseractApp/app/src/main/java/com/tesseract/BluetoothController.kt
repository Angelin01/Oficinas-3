package com.tesseract

import android.bluetooth.BluetoothAdapter

class BluetoothController{

    companion object {
        var bluetoothAdapter: BluetoothAdapter? = null
        var bluetoothService: BluetoothService? = null

        init {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothService = BluetoothService()
        }
    }

}