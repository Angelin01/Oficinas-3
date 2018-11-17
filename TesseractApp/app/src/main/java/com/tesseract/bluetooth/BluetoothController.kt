package com.tesseract.bluetooth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

class BluetoothController: ViewModel() {

	var bluetoothList: MutableLiveData<List<BluetoothDevice>> = MutableLiveData()
	var discoveredBluetoothList: MutableLiveData<List<BluetoothDevice>> = MutableLiveData()

	init {
		bluetoothList.value = ArrayList()
	}

	fun setFoundBluetoothDevices(devices: ArrayList<BluetoothDevice>) {
		discoveredBluetoothList.postValue(devices)
	}

	fun requestPairedBluetooth() {
		val mPairedDevices = BluetoothController.bluetoothAdapter!!.bondedDevices
		val list: ArrayList<BluetoothDevice> = ArrayList()

		if (!mPairedDevices.isEmpty()) {
			for (device: BluetoothDevice in mPairedDevices) {
				list.add(device)
			}
		}
		bluetoothList.postValue(list)
	}


    companion object {
        var bluetoothAdapter: BluetoothAdapter? = null
        var bluetoothService: BluetoothService? = null


        init {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothService = BluetoothService()
        }
    }
}