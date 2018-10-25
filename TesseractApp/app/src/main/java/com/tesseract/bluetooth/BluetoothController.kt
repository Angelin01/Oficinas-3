package com.tesseract.bluetooth

import android.bluetooth.BluetoothAdapter

class BluetoothController {

    public val messageCallback: BluetoothMessageCallback? = null

    companion object {
        var bluetoothAdapter: BluetoothAdapter? = null
        var bluetoothService: BluetoothService? = null


        init {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothService = BluetoothService()
        }
    }
}