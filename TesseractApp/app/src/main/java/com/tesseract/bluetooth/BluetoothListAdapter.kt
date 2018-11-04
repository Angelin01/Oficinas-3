package com.tesseract.bluetooth

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tesseract.R

class BluetoothListAdapter(private var bluetoothList: ArrayList<BluetoothDevice>, var listener: BluetoothListAdapter.OnBluetoothItemClickListener) : RecyclerView.Adapter<BluetoothListAdapter.BluetoothListHolder>() {

	interface OnBluetoothItemClickListener {
		fun onItemClick(item: BluetoothDevice)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothListAdapter.BluetoothListHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_list_item, parent, false)
		return BluetoothListAdapter.BluetoothListHolder(view)
	}

	override fun getItemCount(): Int {
		return bluetoothList.size
	}

	override fun onBindViewHolder(holder: BluetoothListAdapter.BluetoothListHolder, position: Int) {
		holder.bind(bluetoothList[position], listener)
	}

	fun updateList(newBluetoothList: ArrayList<BluetoothDevice>) {
		removeAll()
		for (bluetooth: BluetoothDevice in newBluetoothList) {
			insertItem(bluetooth)
		}
	}

	private fun insertItem(bluetooth: BluetoothDevice) {
		bluetoothList.add(bluetooth)
		notifyItemInserted(itemCount)
	}

	private fun removeAll() {
		bluetoothList.clear()
		notifyDataSetChanged()
	}

	class BluetoothListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bind(item: BluetoothDevice, listener: BluetoothListAdapter.OnBluetoothItemClickListener) = with(itemView) {
			with(itemView.findViewById<TextView>(R.id.textViewBluetoothName)) {
				text = item.name
			}
			with(itemView.findViewById<TextView>(R.id.textViewBluetoothAddress)) {
				text = item.address
			}

			itemView.setOnClickListener { listener.onItemClick(item) }
		}
	}
}