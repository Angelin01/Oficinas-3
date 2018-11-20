#!/usr/bin/python
import signal
from Accelerometer.AccService import AccService
from Audio import Audio
from Communication.BluetoothService import BluetoothService
from FrequencyAnalyzer.SoundAnalyzer import SoundAnalyzer
from Spotify.SpotifyClient import SpotifyClient

# Temp import
from Light.LightFunctions.color_gen import gen_rainbow_gradient
from Light.LightFunctions.handler_creators import create_wave_handler_args, create_fft_freq_color_handler_args
from Light.LightFunctions.handlers import standard_handler, wave_handler, fft_freq_color_handler
from Light.LightFunctions.modifiers import gen_sine_wave
from Light.controller import TimedLightShow
from Light.LightFunctions.handlers import standard_handler, wave_handler
from multiprocessing import Queue

class Tesseract():
	def __init__(self):
		self.bluetooth_queue = Queue()
		bluetooth_leds_queue = Queue()
		bluetooth_acc_queue = Queue()

		self.spotify = SpotifyClient(self)

		# TODO: Create LED control thread
		self.bluetooth_service = BluetoothService(self, self.bluetooth_queue, bluetooth_leds_queue, bluetooth_acc_queue)
		# self.acc_service = AccService(self, bluetooth_acc_queue)

		self.lightConfig(bluetooth_leds_queue)

		self.is_spotify = False

	def run(self):
		self.bluetooth_service.start()
		# self.acc_service.start()
		self.lights = self.light_show.start()


	def stop_services(self, s, f):
		self.bluetooth_service.stop_service()
		# self.acc_service.stop_service()
		self.lights = self.light_show.stop()


	def lightConfig(self, bluetooth_leds_queue):
		n_leds = 80

		# gradient = gen_rainbow_gradient(0, 360, 1, 100)
		# wave = gen_sine_wave(0.5, 3, n_leds)
		# wave_handler_args = create_wave_handler_args(gradient, wave, 10, n_leds)

		fft_args = create_fft_freq_color_handler_args((0, 20, 0), (0, 20, 0), 1)
		self.light_show = TimedLightShow(fft_freq_color_handler, fft_args, 0.1, -1, bluetooth_leds_queue)

		# self.light_show = TimedLightShow(wave_handler, wave_handler_args, 0.05, -1)

if __name__ == '__main__':
	import signal
	tesseract = Tesseract()
	signal.signal(signal.SIGINT, tesseract.stop_services)
	signal.signal(signal.SIGTERM, tesseract.stop_services)
	tesseract.run()
