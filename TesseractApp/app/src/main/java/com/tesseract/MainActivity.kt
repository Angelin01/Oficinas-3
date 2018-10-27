package com.tesseract

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.tesseract.light.LightFragment
import com.tesseract.bluetooth.BluetoothService
import com.tesseract.communication.ConnectionsFragment


class MainActivity : AppCompatActivity() {

    private var bluetoothBroadcastFilter: IntentFilter = IntentFilter(BluetoothService.STATE_CHANGED)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(bluetoothBroadcastReceiver, bluetoothBroadcastFilter)

        val mMainNav: BottomNavigationView = findViewById(R.id.home_nav_bar)

        setFragment(HomeFragment() as Fragment, null)

        mMainNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    setFragment(HomeFragment() as Fragment, "home_fragment")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_light -> {
                    setFragment(LightFragment() as Fragment, null)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_spotify -> {
                    setFragment(SpotifyFragment() as Fragment, null)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_about -> {
                    setFragment(AboutFragment() as Fragment, null)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_connections -> {
                    setFragment(ConnectionsFragment() as Fragment, null)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    setFragment(HomeFragment() as Fragment, null)
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

    }

    public override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(this.bluetoothBroadcastReceiver, bluetoothBroadcastFilter)
    }

    public override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(this.bluetoothBroadcastReceiver)

    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothBroadcastReceiver)
    }

    private fun setFragment(fragment: Fragment, tag: String?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_view_frame, fragment, tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val bluetoothBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothService.STATE_CHANGED) {
                val state = intent.getIntExtra("state", 0)

                when (state) {
                    BluetoothService.BluetoothStates.STATE_CONNECTED.ordinal -> {
                        updateStatusBluetoothView(true)
                        Toast.makeText(context, "Bluetooth Connected", Toast.LENGTH_SHORT).show()
                    }
                    BluetoothService.BluetoothStates.STATE_NONE.ordinal, BluetoothService.BluetoothStates.STATE_CONNECTION_LOST.ordinal -> {
                        updateStatusBluetoothView(false)
                        Toast.makeText(context, "Bluetooth Disconnected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private var mCallback: StatusChanged? = null

    interface StatusChanged {
        fun onStatusChange(connected: Boolean)

    }

    fun updateStatusBluetoothView(connected: Boolean) {
        val homeFragment = this.supportFragmentManager.findFragmentByTag("home_fragment") ?: return

        mCallback = homeFragment as StatusChanged
        (mCallback as HomeFragment).onStatusChange(connected)
    }
}
