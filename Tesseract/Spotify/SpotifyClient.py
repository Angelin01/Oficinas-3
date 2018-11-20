import requests


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

	def next_track(self):
		print('spotify next track')
		requests.post('https://api.spotify.com/v1/me/player/next', headers=self.make_header())

	def previous_track(self):
		print('spotify previous track')
		requests.post('https://api.spotify.com/v1/me/player/previous', headers=self.make_header())

	def pause(self):
		print('spotify pause playback')
		requests.put('https://api.spotify.com/v1/me/player/pause', headers=self.make_header())

	def play(self):
		print('spotify resume playback')
		requests.put('https://api.spotify.com/v1/me/player/play', headers=self.make_header())

	def shuffle(self):
		print('spotify shuffle')
		requests.put('https://api.spotify.com/v1/me/player/shuffle', headers=self.make_header())

	def is_playing(self):
		return False
