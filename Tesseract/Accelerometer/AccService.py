import multiprocessing
from Accelerometer.Accelerometer import Accelerometer
from Accelerometer.AccReading import AccReading
from Spotify.SpotifyClient import SpotifyClient
from threading import Thread


class AccService(multiprocessing.Process):
	def __init__(self, tesseract, bluetooth_queue):
		super().__init__()
		self.tesseract = tesseract
		self.accelerometer = Accelerometer()
		self.bluetooth_queue = bluetooth_queue
		self.spotify_client = SpotifyClient()

		Thread(target=self.read_queue).start()
		self._stop_service = False

	def read_queue(self):
		while not self._stop_service:
			msg = self.bluetooth_queue.get()

			if msg["type"] == "spotify":
				if msg["subtype"] == "disconnect":
					self.spotify_client.is_active = False

				elif msg["subtype"] == "connect":
					self.spotify_client.connect(msg["subtype"]["token"], msg["subtype"]["deviceID"])

			else:
				print("invalid message received by spotify process")

	def stop_service(self):
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
		if self.spotify_client.is_active:
			self.spotify_client.next_track()

	def inclined_left(self):
		if self.spotify_client.is_active:
			self.spotify_client.previous_track()

	def inclined_front(self):
		pass

	def inclined_back(self):
		pass

	def up_and_down(self):
		if self.spotify_client.is_active:
			if self.spotify_client.is_playing():
				self.spotify_client.pause()
			else:
				self.spotify_client.pause()

	def agitated(self):
		if self.spotify_client.is_active:
			self.tesseract.spotify_client.shuffle()
