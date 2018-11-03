package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.tesseract.bluetooth.BluetoothMessageCallback
import com.tesseract.communication.TesseractCommunication
import com.tesseract.communication.WifiCommunication
import com.tesseract.communication.WifiCommunication.requestAvailableWifi
import com.tesseract.wifi.Wifi
import com.tesseract.wifi.WifiListAdapter
import com.tesseract.wifi.WifiListAdapter.OnItemClickListener

class WifiFragment : Fragment(), OnItemClickListener, BluetoothMessageCallback {
	override fun callbackMessageReceiver(values: Any, subtype: String?) {
		val gson = Gson()
		when (subtype) {
			"list" -> {
				wifiList = WifiCommunication.getAvailableWifi(values as ArrayList<String>)
				updateWifiList(wifiList as ArrayList<Wifi>)
			}

		}
	}

	private fun updateWifiList(wifiList: ArrayList<Wifi>) {
		activity!!.runOnUiThread {
			wifiListAdapter.updateList(wifiList)
		}
	}

	override fun onItemClick(item: Wifi) {
		val wifiSelected: Bundle = Bundle()
		wifiSelected.putSerializable("wifi", item)
		val fragment = WifiConnect()
		val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
		fragment.arguments = wifiSelected
		transaction.replace(R.id.home_view_frame, fragment)
		transaction.addToBackStack(null)
		transaction.commit()
	}

	private val clickListener: OnItemClickListener = this
	private lateinit var wifiList: List<Wifi>
	private lateinit var wifiListAdapter: WifiListAdapter

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_wifi, container, false)
		TesseractCommunication.wifiListener = this

		val recyclerViewWifi = view.findViewById<RecyclerView>(R.id.recyclerViewListWifi)

		wifiList = requestAvailableWifi()
		Log.d("TAG", "Wifilist: $wifiList")

		wifiListAdapter = WifiListAdapter(wifiList as ArrayList<Wifi>, clickListener)
		recyclerViewWifi.adapter = wifiListAdapter

		val layoutManager = LinearLayoutManager(this.context)
		recyclerViewWifi.layoutManager = layoutManager

		recyclerViewWifi.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

		return view
	}


}
