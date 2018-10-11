package com.tesseract

import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter

class BluetoothController: ViewModel() {

    var bluetoothAdapter: BluetoothAdapter? = null
    var bluetoothService: BluetoothService? = null

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothService = BluetoothService()
    }

    fun startBluetoothService() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}