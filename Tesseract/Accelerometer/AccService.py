import multiprocessing
from Spotify.SpotifyClient import SpotifyClient
from Accelerometer.Accelerometer import Accelerometer
from Accelerometer.AccReading import AccReading


class AccService(multiprocessing.Process):
	def __init__(self, tesseract):
		super().__init__(self)
		self.tesseract = tesseract
		self.accelerometer = Accelerometer()

		self._stop_service = False

	def stop_self(self):
		self._stop_service = True

	def run(self):
		while not self._stop_service:
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
