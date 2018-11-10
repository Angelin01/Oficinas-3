class Player():

	playing = ...  # type: bool
	shuffle = ...  # type: bool
	position = ...  # type: int
	volume = ...  # type: int

	def __init__(self, playing, shuffle, position, volume):
		self.playing = playing
		self.shuffle = shuffle
		self.position = position
		self.volume = volume