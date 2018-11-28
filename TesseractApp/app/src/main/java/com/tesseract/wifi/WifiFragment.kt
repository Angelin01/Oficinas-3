package com.tesseract.wifi

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.tesseract.R
import com.tesseract.wifi.WifiListAdapter.OnWifiItemClickListener

class WifiFragment : Fragment(), OnWifiItemClickListener, WifiStatusChangeCallback {

	private fun updateWifiList(wifiList: ArrayList<Wifi>) {
		if (activity != null) {
			activity!!.runOnUiThread {
				wifiListAdapter.updateList(wifiList)
			}
		}
	}

	override fun onItemClick(item: Wifi) {
		val wifiSelected = Bundle()
		wifiSelected.putSerializable("wifi", item)
		val fragment = WifiConnect()
		val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
		fragment.arguments = wifiSelected
		transaction.replace(R.id.home_view_frame, fragment)
		transaction.commit()
	}

	private val clickListener: OnWifiItemClickListener = this
	private lateinit var wifiListAdapter: WifiListAdapter

	private lateinit var wifiController: WifiController
	private lateinit var recyclerViewWifi: RecyclerView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_wifi, container, false)

		wifiController = activity?.run { ViewModelProviders.of(this).get(WifiController::class.java) }!!
		wifiController.wifiConnectCallback = this

		configureWifiDeviceList(view)
		configureRefreshButton(view)

		return view
	}

	private fun configureWifiDeviceList(view: View) {
		recyclerViewWifi = view.findViewById(R.id.recyclerViewListWifi)
		wifiListAdapter = WifiListAdapter(wifiController.wifiList.value as ArrayList<Wifi>, clickListener)
		recyclerViewWifi.adapter = wifiListAdapter

		wifiController.wifiList.observe(activity!!, Observer<List<Wifi>> { wifiList ->
			updateWifiList(wifiList as ArrayList<Wifi>)
		})
		wifiController.requestAvailableWifi()

		val layoutManager = LinearLayoutManager(this.context)
		recyclerViewWifi.layoutManager = layoutManager
		recyclerViewWifi.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
	}

	private fun configureRefreshButton(view: View) {
		val refreshButton: Button = view.findViewById(R.id.buttonRefreshWifi)
		refreshButton.setOnClickListener {
			wifiController.requestAvailableWifi()
		}
	}

	override fun onWifiStatusChange(connected: Boolean, ssid: String?) {
		if (activity == null) {
			return
		}

		var message = "Wifi Connected with $ssid"
		if (!connected) {
			message = "Wifi Disconnected"
		}
		activity!!.runOnUiThread {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
		}
	}

}
