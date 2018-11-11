#!/usr/bin/python

from Accelerometer.AccThread import AccThread


class Tesseract:

	def run(self):
		# TODO: Create bluetooth thread
		# TODO: Create LED control thread
		acc_thread = AccThread()
		acc_thread.run()


if __name__ == '__main__':
	tesseract = Tesseract()
	tesseract.run()
