#!/usr/bin/python
import signal
from Accelerometer.AccService import AccService
from Communication.BluetoothService import BluetoothService
from Spotify.SpotifyClient import SpotifyClient


class Tesseract():
	def __init__(self):
		self.spotify = SpotifyClient(self)

		# TODO: Create LED control thread
		self.bluetooth_service = BluetoothService(self)
		self.acc_service = AccService(self)

		self.is_spotify = False

	def run(self):
		self.bluetooth_service.start()
		self.acc_service.start()

	def stop_services(self, s, f):
		self.bluetooth_service.stop_service()
		self.acc_service.stop_service()


if __name__ == '__main__':
	import signal
	tesseract = Tesseract()
	signal.signal(signal.SIGINT, tesseract.stop_services)
	signal.signal(signal.SIGTERM, tesseract.stop_services)
	tesseract.run()
