import requests
import json
from time import sleep


class SpotifyClient:

	def __init__(self, display_queue):
		print('Spotify init')
		self.is_active = False
		self.token = ''
		self.deviceID = '5d5959f8a7e26f1195739478f1cc29dd13162146' #Enjambro
		self.display_queue = display_queue

	def connect(self, token, deviceID):
		self.token = token
		if deviceID:
			self.deviceID = deviceID
		else:
			print('usando ID hard-coded')
		self.is_active = True
		self.display_queue.put(['Spotify', 'conectado!'])
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
		r = requests.post('https://api.spotify.com/v1/me/player/next', headers=self.make_header(), params=payloads)
		return r.status_code in [200, 204]

	def previous_track(self):
		print('spotify previous track')
		payloads = self.make_device_param()
		r = requests.post('https://api.spotify.com/v1/me/player/previous', headers=self.make_header(), params=payloads)
		return r.status_code in [200, 204]

	def pause(self):
		print('spotify pause playback')
		payloads = self.make_device_param()
		r = requests.put('https://api.spotify.com/v1/me/player/pause', headers=self.make_header(), params=payloads)
		return r.status_code in [200, 204]

	def play(self):
		print('spotify resume playback')
		payloads = self.make_device_param()
		r = requests.put('https://api.spotify.com/v1/me/player/play', headers=self.make_header(), params=payloads)
		return r.status_code in [200, 204]

	def shuffle(self):
		print('spotify shuffle')
		payloads = self.make_device_param()
		payloads['state'] = not self.shuffle_state()
		r = requests.put('https://api.spotify.com/v1/me/player/shuffle', headers=self.make_header(), params=payloads)
		return r.status_code in [200, 204]

	def select_playlist(self, playlist_uri):
		print('spotify select playlist ' + playlist_uri)
		playlist_json = '''
					{
						"context_uri": "spotify:playlist:''' + playlist_uri + '''"
					}'''
		r = requests.put('https://api.spotify.com/v1/me/player/play', headers=self.make_header(), data=playlist_json)
		return r.status_code in [200, 204]

	def shuffle_state(self):
		response_json = self.playback_info()
		try:
			return str(response_json["device"]["id"] == self.deviceID) and response_json["shuffle_state"]
		except:
			return None

	def is_playing(self):
		response_json = self.playback_info()
		try:
			return str(response_json["device"]["id"] == self.deviceID) and response_json["is_playing"]
		except:
			return None

	def playback_info(self):
		sleep(0.6)
		r = requests.get('https://api.spotify.com/v1/me/player', headers=self.make_header())

		if r.status_code != 200:
			print('no spotify devices detected')
			return None

		return json.loads(r.text)
