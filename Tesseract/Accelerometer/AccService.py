import multiprocessing
import json
from Accelerometer.Accelerometer import Accelerometer
from Accelerometer.AccReading import AccReading
from Spotify.SpotifyClient import SpotifyClient
from threading import Thread


class AccService(multiprocessing.Process):
	def __init__(self, tesseract, from_bluetooth_queue, display_queue, leds_queue, to_bluetooth_queue):
		super().__init__()
		self.tesseract = tesseract
		self.accelerometer = Accelerometer()
		self.from_bluetooth_queue = from_bluetooth_queue
		self.display_queue = display_queue
		self.leds_queue = leds_queue
		self.to_bluetooth_queue = to_bluetooth_queue
		self.spotify_client = SpotifyClient(self.display_queue)

		self.thread_communication_list = [self.spotify_client]
		self.queue_thread = Thread(target=self.read_queue)

		self._stop_service = False

	def read_queue(self):
		while True:
			msg = self.from_bluetooth_queue.get()

			print("spotify message received!")

			if msg["type"] == "spotify":
				spotify_client = self.thread_communication_list[0]

				if msg["subtype"] == "disconnect":
					spotify_client.is_active = False

				elif msg["subtype"] == "connect":
					spotify_client.connect(msg["value"]["token"], msg["value"]["deviceID"])

				elif msg["subtype"] == "command":
					self.update_display()

			else:
				print("invalid message received by spotify process")

	def stop_service(self):
		self._stop_service = True

	def run(self):
		self.queue_thread.start()

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
			if self.spotify_client.next_track():
				self.update_display()
				self.send_command_to_app("next")

	def inclined_left(self):
		if self.spotify_client.is_active:
			if self.spotify_client.previous_track():
				self.update_display()
				self.send_command_to_app("previous")

	def inclined_front(self):
		pass

	def inclined_back(self):
		pass

	def up_and_down(self):
		if self.spotify_client.is_active:
			if self.spotify_client.is_playing():
				if self.spotify_client.pause():
					result = True
					self.send_command_to_app("pause")
			else:
				if self.spotify_client.play():
					result = True
					self.send_command_to_app("play")

			if result:
				self.update_display()

	def agitated(self):
		if self.spotify_client.is_active:
			if self.spotify_client.shuffle():
				self.update_display()
				self.send_command_to_app("shuffle")
				led_shuffle_command = '''
					{
						"config": "shuffle"
					}
				'''
				self.leds_queue.put(json.loads(led_shuffle_command))

	def update_display(self):
		try:
			playback_info_json = self.spotify_client.playback_info()
			music_name = playback_info_json["item"]["name"]
			artist_name = playback_info_json["item"]["artists"][0]["name"]
			self.display_queue.put([music_name, artist_name])
		except:
			print('update display error')
			pass

	def send_command_to_app(self, command):
		json_command = '''
			{
			   "type": "spotify",
			   "subtype": "command",
			   "value": "''' + command + '''"
			}
		'''
		self.to_bluetooth_queue.put(json.dumps(json_command))
