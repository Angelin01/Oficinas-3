
class Wifi:

	ssid = ...  # type: str
	signal = ...  # type: int
	encryption_type = ...  # type: str
	psk = ...  # type: str

	def __init__(self, ssid, signal, encryption_type, psk):
		self.ssid = ssid
		self.signal = signal
		self.encryption_type = encryption_type
		self.psk = psk
