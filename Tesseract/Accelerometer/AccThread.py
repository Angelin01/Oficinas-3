from threading import Thread

from Spotify.SpotifyClient import SpotifyClient
from Accelerometer.Accelerometer import Accelerometer
from Accelerometer.AccReading import AccReading

class AccThread(Thread):

	def __init__(self, tesseract):
		Thread.__init__(self)
		self.tesseract = tesseract
		self.accelerometer = Accelerometer()

		
	def run(self):
		while True:
			reading = self.accelerometer.wait_for_movement()
			if reading == AccReading.INC_RIGHT:
				self.inclined_right()
			elif reading == AccReading.INC_LEFT:
				self.inclined_left()
			elif reading == AccReading.INC_FRONT:
				self.inclined_front()
			elif reading == AccReading.INC_BACK:
				self.inclined_back()
			elif reading == AccReading.UP_DOWN:
				self.up_and_down()
			elif reading == AccReading.AGITATION:
				self.agitated()


	def inclined_right(self):
		if self.tesseract.is_spotify:
			self.tesseract.spotify.next_track()


	def inclined_left(self):
		if self.tesseract.is_spotify:
			self.tesseract.spotify.previous_track()


	def inclined_front(self):
		pass


	def inclined_back(self):
		pass


	def up_and_down(self):
		pass


	def agitated(self):
		pass
