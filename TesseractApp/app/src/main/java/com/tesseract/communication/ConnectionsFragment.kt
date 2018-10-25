package com.tesseract.communication


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.tesseract.R
import com.tesseract.bluetooth.BluetoothDeviceList


class ConnectionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_connections, container, false)

        val buttonConnectBluetooth: RelativeLayout = view.findViewById(R.id.buttonConnectTesseract)
        buttonConnectBluetooth.setOnClickListener {
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.home_view_frame, BluetoothDeviceList()) // give your fragment container id in first parameter
            transaction.addToBackStack(null)  // if written, this transaction will be added to backstack
            transaction.commit()
        }

        val buttonConnectWifi: RelativeLayout = view.findViewById(R.id.buttonConnectTesseractWifi)
        buttonConnectWifi.setOnClickListener {

        }

        return view
    }

}
