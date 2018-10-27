package com.tesseract

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tesseract.communication.WifiCommunication
import com.tesseract.wifi.Wifi
import com.tesseract.wifi.WifiListAdapter
import com.tesseract.wifi.WifiListAdapter.OnItemClickListener

class WifiFragment : Fragment(), OnItemClickListener {

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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_wifi, container, false)

		val recyclerViewWifi = view.findViewById<RecyclerView>(R.id.recyclerViewListWifi)

		val wifiList = WifiCommunication.getAvailableWifi()

		val wifiListAdapter = WifiListAdapter(wifiList as ArrayList<Wifi>, clickListener)
		recyclerViewWifi.adapter = wifiListAdapter

		val layoutManager = LinearLayoutManager(this.context)
		recyclerViewWifi.layoutManager = layoutManager

		recyclerViewWifi.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

		return view
	}


}
