import multiprocessing
from threading import Thread
from RPi import GPIO
from RPLCD.gpio import CharLCD
from time import sleep


class Display(multiprocessing.Process):
	def __init__(self, write_queue):
		self.write_queue = write_queue
		self.strings_to_write = ['', '']
		self.lcd = CharLCD(pin_rs=18, pin_rw=None, pin_e=23, pins_data=[12, 16, 20, 21], numbering_mode=GPIO.BCM, cols=16, rows=2)

	def run(self):
		pass

	def update_to_write(self):
		pass
