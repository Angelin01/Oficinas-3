package com.tesseract.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService : Service() {

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	private val mAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

	private var mConnectThread: ConnectThread? = null
	private var mConnectedThread: ConnectedThread? = null


	/**
	 * Return the current connection mState.
	 */
	@get:Synchronized
	internal var mState: BluetoothStates = BluetoothStates.STATE_NONE
		private set

	init {
		mState = BluetoothStates.STATE_NONE
	}

	/**
	 * Return the current connection state.
	 */
	@Synchronized
	internal fun getState(): BluetoothStates {
		return mState
	}

	/**
	 * Update UI title according to the current mState of the chat connection
	 */
	@Synchronized
	private fun broadCastChanges() {
		mState = mState

		val intent = Intent()
		intent.action = STATE_CHANGED
		intent.putExtra("state", mState.ordinal)

		LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	@Synchronized
	fun start() {
		Log.d(TAG, "start")

		cancelConnectThread()
		cancelAnyThreadConnected()

		broadCastChanges()
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device The BluetoothDevice to connect
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	@Synchronized
	internal fun connect(device: BluetoothDevice, secure: Boolean) {
		Log.d(TAG, "connect to: $device")

		cancelAnyThreadTryingToConnect()
		cancelAnyThreadConnected()

		mConnectThread = ConnectThread(device, secure)
		mConnectThread!!.start()

		broadCastChanges()
	}

	private fun cancelAnyThreadConnected() {
		if (mConnectedThread != null) {
			mConnectedThread!!.cancel()
			mConnectedThread = null
		}
	}

	private fun cancelAnyThreadTryingToConnect() {
		if (mState == BluetoothStates.STATE_CONNECTING) {
			cancelConnectThread()
		}
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 *
	 * @param socket The BluetoothSocket on which the connection was made
	 */
	@Synchronized
	private fun connected(socket: BluetoothSocket, socketType: String) {
		Log.d(TAG, "connected, Socket Type:$socketType")

		cancelConnectThread()
		cancelAnyThreadConnected()

		mConnectedThread = ConnectedThread(socket, socketType)
		mConnectedThread!!.start()

		broadCastChanges()
	}

	/**
	 * Stop all threads
	 */
	@Synchronized
	internal fun stop() {
		Log.d(TAG, "stop")

		cancelConnectThread()
		cancelAnyThreadConnected()
		mState = BluetoothStates.STATE_NONE

		broadCastChanges()
	}

	private fun cancelConnectThread() {
		if (mConnectThread != null) {
			mConnectThread!!.cancel()
			mConnectThread = null
		}
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 *
	 * @param out The bytes to write
	 * @see ConnectedThread.write
	 */
	fun write(out: ByteArray) {
		var connectedThread: ConnectedThread? = null
		synchronized(this) {
			if (mState != BluetoothStates.STATE_CONNECTED) return
			connectedThread = mConnectedThread
		}

		connectedThread!!.write(out)
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private fun connectionFailed() {
		mState = BluetoothStates.STATE_CONNECTION_FAILED
		broadCastChanges()

		this@BluetoothService.start()
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private fun connectionLost() {
		mState = BluetoothStates.STATE_CONNECTION_LOST
		broadCastChanges()

		this@BluetoothService.start()
	}

	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private inner class ConnectThread internal constructor(mmDevice: BluetoothDevice, secure: Boolean) : Thread() {
		private val mmSocket: BluetoothSocket?
		private val mSocketType: String

		init {
			var tmp: BluetoothSocket? = null
			mSocketType = if (secure) "Secure" else "Insecure"

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = if (secure) {
					mmDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE)
				} else {
					mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE)
				}
			} catch (e: IOException) {
				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e)
			}

			mmSocket = tmp
			mState = BluetoothStates.STATE_CONNECTING
		}

		override fun run() {
			Log.i(TAG, "BEGIN mConnectThread SocketType:$mSocketType")
			name = "ConnectThread$mSocketType"

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery()

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket!!.connect()
			} catch (e: IOException) {
				// Close the socket
				try {
					mmSocket!!.close()
				} catch (e2: IOException) {
					Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e2)
				}

				connectionFailed()
				return
			}

			// Reset the ConnectThread because we're done
			synchronized(this@BluetoothService) {
				mConnectThread = null
			}

			connected(mmSocket, mSocketType)
		}

		internal fun cancel() {
			try {
				mmSocket!!.close()
			} catch (e: IOException) {
				Log.e(TAG, "close() of connect $mSocketType socket failed", e)
			}

		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private inner class ConnectedThread internal constructor(private val mmSocket: BluetoothSocket, socketType: String) : Thread() {
		private val mmInStream: InputStream?
		private val mmOutStream: OutputStream?

		init {
			Log.d(TAG, "create ConnectedThread: $socketType")
			var tmpIn: InputStream? = null
			var tmpOut: OutputStream? = null

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = mmSocket.inputStream
				tmpOut = mmSocket.outputStream
			} catch (e: IOException) {
				Log.e(TAG, "temp sockets not created", e)
			}

			mmInStream = tmpIn
			mmOutStream = tmpOut
			mState = BluetoothStates.STATE_CONNECTED
		}

		override fun run() {
			Log.i(TAG, "BEGIN mConnectedThread")
			val bufferSize = 1024
			val buffer = ByteArray(bufferSize)

			// Keep listening to the InputStream while connected
			while (mState == BluetoothStates.STATE_CONNECTED) {
				try {
					// Read from the InputStream
					val input = readInput(buffer, bufferSize)

					tesseractCommunicationCallback!!.callbackMessageReceiver(input, null)

				} catch (e: IOException) {
					Log.e(TAG, "disconnected", e)
					connectionLost()
					break
				}

			}
		}

		private fun readInput(buffer: ByteArray, bufferSize: Int): String {
			var received = ""
			var bytes: Int
			do {
				bytes = mmInStream!!.read(buffer)
				var input = String(buffer)
				input = input.substring(0, bytes)
				received += input
			} while (!received.contains("--end_of_message"))

			return received.replace("--end_of_message", "")
		}


		/**
		 * Write to the connected OutStream.
		 *
		 * @param buffer The bytes to write
		 */
		internal fun write(buffer: ByteArray) {
			try {
				mmOutStream!!.write(buffer)

			} catch (e: IOException) {
				Log.e(TAG, "Exception during write", e)
			}

		}

		internal fun cancel() {
			try {
				mmSocket.close()
			} catch (e: IOException) {
				Log.e(TAG, "close() of connect socket failed", e)
			}

		}
	}

	companion object {

		private const val TAG = "BluetoothService"

		// Unique UUID for this application
		private val MY_UUID_SECURE: UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee")
		private val MY_UUID_INSECURE: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

		const val STATE_CHANGED = "com.you.tesseract.BLUETOOTH_CONNECTION_CHANGED"

		private var tesseractCommunicationCallback: BluetoothMessageCallback? = null

		fun setListener(listener: BluetoothMessageCallback) {
			tesseractCommunicationCallback = listener
		}

	}


	enum class BluetoothStates {
		STATE_NONE, STATE_CONNECTING, STATE_CONNECTED, STATE_CONNECTION_FAILED, STATE_CONNECTION_LOST
	}


}