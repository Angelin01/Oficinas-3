
class Music:
	name = ...  # type: str
	band_name = ...  # type: str
	album_cover_url = ...  # type: str
	duration = ...  # type: int

	def __init__(self, name, band_name, album_cover_url, duration):
		self.name = name
		self.band_name = band_name
		self.album_cover_url = album_cover_url
		self.duration = duration
