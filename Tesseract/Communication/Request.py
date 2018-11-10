
class Request:
	type = ...  # type: str
	subtype = ...  # type: str

	def __init__(self, type, subtype, value):
		self.type = type
		self.subtype = subtype
		self.value = value
