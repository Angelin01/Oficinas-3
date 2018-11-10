import requests

class SpotifyClient():
    def __init__(self, token):
        self.token = token

    def make_header(self):
        header = { "Authorization" : "Bearer " + self.token }
        return header

    def next_track(self):
        r = requests.post('https://api.spotify.com/v1/me/player/next', headers = self.make_header())

    def previous_track(self):
        r = requests.post('https://api.spotify.com/v1/me/player/previous', headers = self.make_header())