import spidev
import ws2812

import threading
import time

from LEDstripController.LightFunctions.random import gen_random
from LEDstripController.light_config_parser import parse_light_config
from LEDstripController.tesseract_light_face import TesseractLightFace


class TimedLightShow(threading.Thread):

	def __init__(self, bluetooth_queue, acc_queue):
		"""
		:param bluetooth_queue: The queue that receives stuff from the Bluetooth service
		:param acc_queue: The queue that receives stuff from the Accelerometer service
		"""
		super().__init__()

		self.bluetooth_queue = bluetooth_queue
		self.acc_queue = acc_queue

		self.light_faces = {
			0: TesseractLightFace(),
			1: TesseractLightFace(),
			2: TesseractLightFace(),
			3: TesseractLightFace()
		}

		self.stop_event = threading.Event()

		self.controller_interval = 0.010  # 10 ms
		self.controller_interval_fraction = self.controller_interval / 5  # To be used by the timer.

		self.next_strip_update = 0

		self.spi = spidev.SpiDev()
		self.spi.open(0, 0)

		self.current_time = 0

		self.is_shuffling = False

		self.acc_queue_watcher = threading.Thread(target=self.acc_queue_msg_watcher)
		self.bluetooth_queue_watcher = threading.Thread(target=self.bluetooth_queue_msg_watcher)

		self.lock = threading.Lock()

	def bluetooth_queue_msg_watcher(self):

		while True:
			msg = self.acc_queue.get()

			parsed_configs = parse_light_config(msg)

			for config in parsed_configs:
				self.update_face_config(**config)

	def acc_queue_msg_watcher(self):

		while True:
			msg = self.acc_queue.get()
			if 'type' not in msg:
				continue

			if msg['type'] == 'shuffle':
				with self.lock:
					self.is_shuffling = True

				self.handle_shuffle_effect()

				with self.lock:
					self.is_shuffling = False

	def handle_shuffle_effect(self):
		"""
		When a message saying that the shuffle was enabled,
		interrupts the current led animations, and performs a special random animation.
		:return:
		"""

		random_sequence_len = 20
		random_sequence = gen_random(20, random_sequence_len)

		sleep_time = 0.015
		step = 0

		while step < 40:
			ws2812.write2812(self.spi, random_sequence[step % random_sequence_len] * 4)
			step += 1
			time.sleep(sleep_time)
			sleep_time -= 0.0025

		for i in range(2):
			time.sleep(0.15)
			ws2812.write2812(self.spi, random_sequence[step % random_sequence_len] * 4)
			step += 1

	def update_face_config(self, **kwargs):
		"""
		:param kwargs: Must contain fields named:
			'face_id', 'handler_id', 'update_interval', 'config_args', 'modifier_id'.

			The 'face_id' attribute is used by this controller and must be and int.
			The others are used by the TesseractLightFace object. See it for more details.

		:return: Nothing.
		"""

		self.light_faces[kwargs['face_id']].set_new_config(
			kwargs['pattern'],
			kwargs['update_interval'],
			kwargs['config_args'],
			kwargs['modifier_id']
		)

	def run(self):

		self.acc_queue_watcher.start()
		self.bluetooth_queue_watcher.start()

		while True:

			with self.lock:
				is_shuffling = self.is_shuffling

			if is_shuffling:
				time.sleep(self.controller_interval)
				continue

			result = [self.light_faces[i].get_new_sequence() for i in range(4)]

			# Sleeps the thread for a moment.
			time.sleep(self.controller_interval)

			# Sending results to the strip.
			ws2812.write2812(self.spi, result)

			if self.stop_event.is_set():
				break

	def stop(self):
		self.stop_event.set()
