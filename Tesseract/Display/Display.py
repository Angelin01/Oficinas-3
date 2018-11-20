import multiprocessing
from threading import Thread
from RPi import GPIO
from RPLCD.gpio import CharLCD
from asyncio import sleep


class Display(multiprocessing.Process):
	def __init__(self, write_queue):
		self.write_queue = write_queue
		self.strings_to_write = ['', '']
		self.changed = True
		self.lcd = CharLCD(pin_rs=18, pin_rw=None, pin_e=23, pins_data=[12, 16, 20, 21], numbering_mode=GPIO.BCM, cols=16, rows=2, auto_linebreaks=False)
		self.watch_queue = Thread(target=self.update_to_write)

	def run(self):
		self.watch_queue.start()

		while True:
			if self.changed:
				lcd.clear()
				# Update write values
				self.changed = False

			# Write stuff on LCD here
			sleep(0.2)

	def update_to_write(self):
		while True:
			msg = self.write_queue.get()
			if msg[0] != self.strings_to_write[0] or msg[1] != self.strings_to_write[1]:
				self.strings_to_write[0], self.strings_to_write[1] = msg[0], msg[1]
				self.changed = True
