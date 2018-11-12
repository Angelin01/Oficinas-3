import requests


class SpotifyClient():

	def __init__(self, tesseract):
		print('Spotify init')
		# TODO: Token must be received from Bluetooth
		self.token = 'BQCYevuKiMyi_iDe9odDdcYqQYvMy5gKroN4bQm2UVSXE-rqXRR-xhhIx-EJVS7skVKMp7EjcRRRw_f10sP39XKMRFbgcCRRJ4Hgms39jYMgRDIXRELWmGG5HN9mteOWaQDKx8XpasYhs_EwS8nITqTFYx0o7Mc'
		self.tesseract = tesseract


	def make_header(self):
		header = { "Authorization" : "Bearer " + self.token }
		return header


	def next_track(self):
		print('next')
		r = requests.post('https://api.spotify.com/v1/me/player/next', headers = self.make_header())
		print(r.text)


	def previous_track(self):
		r = requests.post('https://api.spotify.com/v1/me/player/previous', headers = self.make_header())
