package com.tesseract

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.tesseract.bluetooth.*
import com.tesseract.light.LightFragment
import com.tesseract.communication.ConnectionsFragment
import com.tesseract.communication.TesseractCommunication
import com.tesseract.spotify.SpotifyController
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    //region [Static variables]
    companion object {
        val spotifyRequestCode = 1337
    }
    //endregion

    private var bluetoothBroadcastFilter: IntentFilter = IntentFilter(BluetoothService.STATE_CHANGED)
	private var bluetoothDiscoverFilter: IntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
	private var bluetoothDiscoverFinishedFilter: IntentFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

	private var foundBluetoothDeviceList: ArrayList<BluetoothDevice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(bluetoothBroadcastReceiver, bluetoothBroadcastFilter)
	    registerReceiver(bluetoothBroadcastReceiver, bluetoothDiscoverFilter)
	    registerReceiver(bluetoothBroadcastReceiver, bluetoothDiscoverFinishedFilter)
        BluetoothService.setListener(TesseractCommunication as BluetoothMessageCallback)

        val mMainNav: BottomNavigationView = findViewById(R.id.home_nav_bar)

        setFragment(R.id.home_view_frame, HomeFragment() as Fragment, tag = null, addToBackStack = false)

        mMainNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_light -> {
                    setFragment(R.id.home_view_frame, LightFragment() as Fragment, "light")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_spotify -> {
                    setFragment(R.id.home_view_frame, SpotifyFragment() as Fragment, "spotify")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_about -> {
                    setFragment(R.id.home_view_frame, AboutFragment() as Fragment, "about")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_connections -> {
                    setFragment(R.id.home_view_frame, ConnectionsFragment() as Fragment, "connection")
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    setFragment(R.id.home_view_frame, HomeFragment() as Fragment, "home")
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == spotifyRequestCode)
        {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            if (response.type == AuthenticationResponse.Type.TOKEN)
            {
                //region Message box
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Deu boa!")
                builder.setMessage("Spotify conectado!")
                val alerta = builder.create()
                alerta.show()
                //endregion

                SpotifyController.setSpotifyConnection(response.accessToken)
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

	override fun onBackPressed() {
		if (supportFragmentManager.backStackEntryCount > 0) {
			supportFragmentManager.popBackStack()
		} else {
			super.onBackPressed()
		}

		val bottomNavigationView = this.findViewById(R.id.home_nav_bar) as BottomNavigationView
		if (bottomNavigationView.selectedItemId == R.id.nav_home) {
			super.onBackPressed()
		} else {
			bottomNavigationView.selectedItemId = R.id.nav_home
		}
	}

    private fun setFragment(frameId: Int, fragment: Fragment, tag: String?, addToBackStack: Boolean = true) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(frameId, fragment, tag)

	    val stackedFragment = this.supportFragmentManager.findFragmentByTag(tag)
	    if (addToBackStack && stackedFragment == null) {
		    transaction.addToBackStack(tag)
	    }

	    val currentFragment: Fragment? = getCurrentFragment()
	    if (stackedFragment == null) {
		    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
	    }
	    else if (currentFragment != null) {
		    if (currentFragment.tag != stackedFragment.tag) {
			    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		    }
	    }
        transaction.commit()
    }

	private fun getCurrentFragment(): Fragment? {
		val fragments = supportFragmentManager.fragments
		if (supportFragmentManager.backStackEntryCount == 0) {
			return null
		}

		return fragments[fragments.size - 1]
	}

	private val bluetoothBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
	        when (action) {
		        BluetoothService.STATE_CHANGED -> onBluetoothStatusChange(intent, context)
		        BluetoothDevice.ACTION_FOUND -> onBluetoothDevicesFound()
		        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> onBluetoothDiscoverFinished()
	        }
        }

		private fun onBluetoothDiscoverFinished() {
			sendFoundDevicesToList()
		}

		private fun onBluetoothDevicesFound() {
			val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
			if (device.bondState != BluetoothDevice.BOND_BONDED) {

				foundBluetoothDeviceList.add(device)
			}
		}

		private fun onBluetoothStatusChange(intent: Intent, context: Context) {
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

	private fun sendFoundDevicesToList() {
		val bluetoothController: BluetoothController = this.run { ViewModelProviders.of(this).get(BluetoothController::class.java) }!!
//		bluetoothController.setFoundBluetoothDevices(foundBluetoothDeviceList)
	}

	private var mCallback: BluetoothStatusChangeCallback? = null

	fun updateStatusBluetoothView(connected: Boolean) {
	    notifyHomeFragment(connected)
	    notifyConnectinoFragment(connected)
    }

	private fun notifyHomeFragment(connected: Boolean) {
		val homeFragment = this.supportFragmentManager.findFragmentByTag("home") ?: return

		mCallback = homeFragment as BluetoothStatusChangeCallback
		(mCallback as HomeFragment).onStatusChange(connected)
	}

	private fun notifyConnectinoFragment(connected: Boolean) {
		val connectionFragment = this.supportFragmentManager.findFragmentByTag("connection") ?: return

		mCallback = connectionFragment as BluetoothStatusChangeCallback
		(mCallback as ConnectionsFragment).onStatusChange(connected)
	}
}
