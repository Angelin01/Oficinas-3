#!/usr/bin/python

from Accelerometer.AccThread import AccThread


class Tesseract:

	def __init__(self):
		# TODO: Create bluetooth thread
		# TODO: Create LED control thread
		self.spotify = SpotifyClient(self)
		self.acc_thread = AccThread(self)

		self.is_spotify = True


	def run(self):
		acc_thread.run()


if __name__ == '__main__':
	tesseract = Tesseract()
	tesseract.run()
