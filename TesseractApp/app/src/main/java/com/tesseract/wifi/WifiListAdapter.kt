package com.tesseract.wifi

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.tesseract.R

class WifiListAdapter(private val wifiList: ArrayList<Wifi>, var listener: OnItemClickListener) : RecyclerView.Adapter<WifiListAdapter.WifiListHolder>() {

	interface OnItemClickListener {
		fun onItemClick(item: Wifi)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiListHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.wifi_list_item, parent, false)
		return WifiListHolder(view)
	}

	override fun getItemCount(): Int {
		return wifiList.size
	}

	override fun onBindViewHolder(holder: WifiListHolder, position: Int) {
		holder.bind(wifiList[position], listener)
	}

	fun updateList(wifi: Wifi) {
		insertItem(wifi)
	}

	private fun insertItem(wifi: Wifi) {
		wifiList.add(wifi)
		notifyItemInserted(itemCount)
	}

	private fun removeItem(position: Int) {
		wifiList.removeAt(position)
		notifyItemRemoved(position)
		notifyItemRangeChanged(position, wifiList.size)
	}

	class WifiListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bind(item: Wifi, listener: OnItemClickListener) = with(itemView) {
			with(itemView.findViewById<TextView>(R.id.textViewWifiSSID)) {
				text = item.ssid
			}
			with(itemView.findViewById<TextView>(R.id.textViewWifiSecurity)) {
				text = item.encryption_type
			}
			with(itemView.findViewById<ImageView>(R.id.imageViewWifiStrength)) {
				setBackgroundResource(R.drawable.ic_wifi_light)
			}

			itemView.setOnClickListener { listener.onItemClick(item) }
		}
	}

}
