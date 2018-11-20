import requests
import json


class SpotifyClient:

	def __init__(self):
		print('Spotify init')
		self.is_active = False
		self.token = ''
		self.deviceID = ''

	def connect(self, token, deviceID):
		self.token = token
		self.deviceID = deviceID
		self.is_active = True
		print('Spotify connected!')
		print('Token: ' + self.token)
		print('Device ID: ' + self.deviceID)

	def make_header(self):
		header = {
			"Authorization": "Bearer " + self.token
		}
		return header

	def make_device_param(self):
		param = {
			"device_id": str(self.deviceID)
		}
		return param

	def next_track(self):
		print('spotify next track')
		payloads = self.make_device_param()
		requests.post('https://api.spotify.com/v1/me/player/next', headers=self.make_header(), params=payloads)

	def previous_track(self):
		print('spotify previous track')
		payloads = self.make_device_param()
		requests.post('https://api.spotify.com/v1/me/player/previous', headers=self.make_header(), params=payloads)

	def pause(self):
		print('spotify pause playback')
		payloads = self.make_device_param()
		requests.put('https://api.spotify.com/v1/me/player/pause', headers=self.make_header(), params=payloads)

	def play(self):
		print('spotify resume playback')
		payloads = self.make_device_param()
		requests.put('https://api.spotify.com/v1/me/player/play', headers=self.make_header(), params=payloads)

	def shuffle(self):
		print('spotify shuffle')
		payloads = self.make_device_param()
		payloads['state'] = not self.shuffle_state()
		r = requests.put('https://api.spotify.com/v1/me/player/shuffle', headers=self.make_header(), params=payloads)
		if r.status_code not in [200, 204]:
			print(r.status_code)
			print(json.loads(r.text))

	def shuffle_state(self):
		response_json = self.playback_info()
		try:
			return str(response_json["device"]["id"] == self.deviceID) and response_json["shuffle_state"]
		except:
			return False

	def is_playing(self):
		response_json = self.playback_info()
		try:
			return str(response_json["device"]["id"] == self.deviceID) and response_json["is_playing"]
		except:
			return False

	def playback_info(self):
		r = requests.get('https://api.spotify.com/v1/me/player', headers=self.make_header())

		if r.status_code != 200:
			print('no spotify devices detected')
			return NULL

		return json.loads(r.text)
