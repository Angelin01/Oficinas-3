package com.tesseract.bluetooth


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.tesseract.R

class BluetoothDeviceList : Fragment(), BluetoothListAdapter.OnBluetoothItemClickListener {

	override fun onItemClick(device: BluetoothDevice) {
		val address: String = device.address
		BluetoothController.bluetoothAdapter!!.cancelDiscovery()
		connectDevice(address, true)
	}

	override fun onStart() {
		super.onStart()
		if (!BluetoothController.bluetoothAdapter!!.isEnabled) {
			val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
			this.activity!!.startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH)
		}
	}

	override fun onResume() {
		super.onResume()
		if (BluetoothController.bluetoothService != null) {
			if (BluetoothController.bluetoothService!!.getState() == BluetoothService.BluetoothStates.STATE_NONE) {
				BluetoothController.bluetoothService!!.start()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)

		if (BluetoothController.bluetoothAdapter == null) {
			val activity = activity
			Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show()
			activity!!.finish()
		}
	}

	private lateinit var bluetoothController: BluetoothController
	private lateinit var bluetoothListAdapter: BluetoothListAdapter
	private val clickListener: BluetoothListAdapter.OnBluetoothItemClickListener = this


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_bluetooth_device_list, container, false)

		if (BluetoothController.bluetoothAdapter == null) {
			Toast.makeText(this.context, "This device does not support bluetooth", Toast.LENGTH_SHORT).show()
			return view
		}

		bluetoothController = activity?.run { ViewModelProviders.of(this).get(BluetoothController::class.java) }!!

		bluetoothListAdapter = BluetoothListAdapter(bluetoothController.bluetoothList.value as ArrayList<BluetoothDevice>, clickListener)
		val recyclerViewBluetooth = view.findViewById<RecyclerView>(R.id.recyclerViewListBluetooth)
		recyclerViewBluetooth.adapter = bluetoothListAdapter

		bluetoothController.bluetoothList.observe(activity!!, Observer<List<BluetoothDevice>> { bluetoothList ->
			updateWifiList(bluetoothList as ArrayList<BluetoothDevice>)
		})
		bluetoothController.requestPairedBluetooth()

		val layoutManager = LinearLayoutManager(this.context)
		recyclerViewBluetooth.layoutManager = layoutManager
		recyclerViewBluetooth.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

		val scanButton: Button = view.findViewById(R.id.bluetooth_button_refresh)
		scanButton.setOnClickListener {
			doDiscovery()
			bluetoothController.requestPairedBluetooth()
		}

		return view
	}

	private fun updateWifiList(bluetoothList: ArrayList<BluetoothDevice>) {
		activity!!.runOnUiThread {
			bluetoothListAdapter.updateList(bluetoothList)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			REQUEST_ENABLE_BLUETOOTH -> {
				if (resultCode == Activity.RESULT_OK) {
					if (BluetoothController.bluetoothAdapter!!.isEnabled) {
						Toast.makeText(this.context, "Bluetooth has been enabled", Toast.LENGTH_SHORT).show()
					} else {
						Toast.makeText(this.context, "Bluetooth has been disabled", Toast.LENGTH_SHORT).show()
					}
				} else if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(this.context, "Bluetooth enabling has been canceled", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	/**
	 * Establish connection with other device
	 *
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	private fun connectDevice(address: String, secure: Boolean) {
		val device: BluetoothDevice = BluetoothController.bluetoothAdapter!!.getRemoteDevice(address)
		BluetoothController.bluetoothService!!.connect(device, secure)
	}

	private fun doDiscovery() {
		if (BluetoothController.bluetoothAdapter!!.isDiscovering) {
			BluetoothController.bluetoothAdapter!!.cancelDiscovery()
		}

		BluetoothController.bluetoothAdapter!!.startDiscovery()
	}

	companion object {
		//        private const val REQUEST_CONNECT_DEVICE_SECURE = 1
//        private const val REQUEST_CONNECT_DEVICE_INSECURE = 2
		private const val REQUEST_ENABLE_BLUETOOTH = 3
//        private const val EXTRA_DEVICE_ADDRESS = "device_address"

	}

}
