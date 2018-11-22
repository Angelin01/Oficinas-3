#!/usr/bin/python
import signal
from Accelerometer.AccService import AccService
from Audio import Audio
from Communication.BluetoothService import BluetoothService
from FrequencyAnalyzer.SoundAnalyzer import SoundAnalyzer
from Display.Display import Display


from Light.controller import TimedLightShow
from multiprocessing import Queue

class Tesseract():
	def __init__(self):
		self.bluetooth_queue = Queue()
		bluetooth_leds_queue = Queue()
		bluetooth_acc_queue = Queue()
		acc_bluetooth_queue = Queue()
		self.acc_leds_queue = Queue()
		display_queue = Queue()

		self.bluetooth_service = BluetoothService(self, self.bluetooth_queue, bluetooth_leds_queue, bluetooth_acc_queue, display_queue, acc_bluetooth_queue)
		self.acc_service = AccService(self, bluetooth_acc_queue, display_queue, self.acc_leds_queue, acc_bluetooth_queue)

		self.display = Display(display_queue)

		self.lightConfig(bluetooth_leds_queue)

	def run(self):
		self.display.start()
		self.bluetooth_service.start()
		self.acc_service.start()
		self.lights = self.light_show.start()

	def stop_services(self, s, f):
		self.bluetooth_service.stop_service()
		self.acc_service.stop_service()
		self.lights = self.light_show.stop()


	def lightConfig(self, bluetooth_leds_queue):
		self.light_show = TimedLightShow(bluetooth_leds_queue, self.acc_leds_queue)


if __name__ == '__main__':
	import signal
	tesseract = Tesseract()
	signal.signal(signal.SIGINT, tesseract.stop_services)
	signal.signal(signal.SIGTERM, tesseract.stop_services)
	tesseract.run()
