import multiprocessing
from threading import Thread
from threading import Lock
from RPi import GPIO
from RPLCD.gpio import CharLCD
from time import sleep
from ctypes import c_bool


class Display(multiprocessing.Process):
	def __init__(self, write_queue):
		super().__init__()
		self.write_queue = write_queue

		self.strings_to_write = ['', '']
		self.changed = multiprocessing.Value(c_bool, True)

		self.lcd = CharLCD(pin_rs=18, pin_rw=None, pin_e=23, pins_data=[12, 16, 20, 21], numbering_mode=GPIO.BCM,
		                   cols=16, rows=2, auto_linebreaks=False)
		self.lock = Lock()
		self.lcd.cursor_mode = 'hide'
		self.watch_queue = Thread(target=self.update_to_write, args=(self.changed, self.lock))

	def run(self):
		self.watch_queue.start()
		top_scroll, bottom_scroll = False, False
		top_string = self.strings_to_write[0]
		bottom_string = self.strings_to_write[1]
		i, j = 0, 0

		while True:
			try:
				if self.changed.value:
					with self.lock:
						# i, j = 0, 0
						self.lcd.clear()

						if True:
						# if len(self.strings_to_write[0]) <= 16:
							top_scroll = False
							self.lcd.cursor_pos = (0, 0)
							self.lcd.write_string(self.strings_to_write[0][:16])
						# else:
						# 	top_scroll = True
						# 	top_string = self.strings_to_write[0] + '  ' + self.strings_to_write[0]
						# 	max_i = len(self.strings_to_write[0]) + 2
						# 	top_freeze_ticks = 4

						# if len(self.strings_to_write[1]) <= 16:
							bottom_scroll = False
							self.lcd.cursor_pos = (1, 0)
							self.lcd.write_string(self.strings_to_write[1][:16])

						# else:
						# 	bottom_scroll = True
						# 	bottom_string = self.strings_to_write[1] + '  ' + self.strings_to_write[1]
						# 	max_j = len(self.strings_to_write[1]) + 2
						# 	bottom_freeze_ticks = 4

						self.changed.value = False

				# if top_scroll:
				# 	self.lcd.cursor_pos = (0, 0)
				# 	self.lcd.write_string(top_string[i:i+16])
				# 	if i == 0:
				# 		if top_freeze_ticks > 0:
				# 			top_freeze_ticks -= 1
				# 		else:
				# 			top_freeze_ticks = 4
				# 			i = 1
				# 	else:
				# 		i = (i + 1) % max_i
				#
				# if bottom_scroll:
				# 	self.lcd.cursor_pos = (1, 0)
				# 	self.lcd.write_string(bottom_string[j:j+16])
				# 	if j == 0:
				# 		if bottom_freeze_ticks > 0:
				# 			bottom_freeze_ticks -= 1
				# 		else:
				# 			bottom_freeze_ticks = 4
				# 			j = 1
				# 	else:
				# 		j = (j + 1) % max_j

				sleep(0.2)
			except Exception as exception:
				print('display service exception: ', exception)

	def update_to_write(self, changed, lock):
		while True:
			try:
				msg = self.write_queue.get()
				if msg[0] != self.strings_to_write[0] or msg[1] != self.strings_to_write[1]:
					with lock:
						self.strings_to_write[0], self.strings_to_write[1] = msg[0], msg[1]
						changed.value = True

				sleep(2)
			except Exception as exception:
				print('invalid message received by display process: ', exception)
