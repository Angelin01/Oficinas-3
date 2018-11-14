#!/usr/bin/python

from Accelerometer.AccService import AccService
from Communication.BluetoothService import BluetoothService
from Spotify.SpotifyClient import SpotifyClient


class Tesseract:
	def __init__(self):
		self.spotify = SpotifyClient(self)

		# TODO: Create LED control thread
		self.bluetooth_service = BluetoothService(self)
		self.acc_thread = AccService(self)

		self.is_spotify = False

	def run(self):
		self.bluetooth_service.start()
		self.acc_thread.start()


if __name__ == '__main__':
	tesseract = Tesseract()
	tesseract.run()
