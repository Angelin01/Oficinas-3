package com.tesseract

import android.app.AlertDialog
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
import com.tesseract.bluetooth.BluetoothMessageCallback
import com.tesseract.light.LightFragment
import com.tesseract.bluetooth.BluetoothService
import com.tesseract.communication.ConnectionsFragment
import com.tesseract.communication.TesseractCommunication

class MainActivity : AppCompatActivity() {

    //region [Static variables]
    companion object {
        var spotifyToken: String = ""
    }
    //endregion

    private var bluetoothBroadcastFilter: IntentFilter = IntentFilter(BluetoothService.STATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(bluetoothBroadcastReceiver, bluetoothBroadcastFilter)
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
        if (requestCode == 29384)
            return

        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 1337)
        {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            if (response.type == AuthenticationResponse.Type.TOKEN)
            {
                spotifyToken = response.accessToken

                //Cria o gerador do AlertDialog
                val builder = AlertDialog.Builder(this)
                //define o titulo
                builder.setTitle("Deu boa!")
                //define a mensagem
                builder.setMessage("Spotify conectado!")
                //cria o AlertDialog
                val alerta = builder.create()
                //Exibe
                alerta.show()

                SpotifyFragment.sendSpotifyConnectionRequest()
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
        val homeFragment = this.supportFragmentManager.findFragmentByTag("home") ?: return

        mCallback = homeFragment as StatusChanged
        (mCallback as HomeFragment).onStatusChange(connected)
    }
}
