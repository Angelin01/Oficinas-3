
class Light:
	name = ...  # type: str
	colors = ...  # type: list
	description = ...  # type: str
	colors_parameters = ...  # type: list

	def __init__(self, name, description, colors, colors_parameters):
		self.name = name
		self.description = description
		self.colors = colors
		self.colors_parameters = colors_parameters
