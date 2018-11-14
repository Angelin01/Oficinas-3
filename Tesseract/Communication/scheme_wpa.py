import itertools
import netifaces
import re
from time import sleep
from wifi import Cell, Scheme
import wifi.subprocess_compat as subprocess
from pbkdf2 import PBKDF2
from wifi.utils import ensure_file_exists


def configuration(cell, passkey=None):
	"""
	Returns a dictionary of configuration options for cell

	Asks for a password if necessary
	"""
	if not cell.encrypted:
		return {
			'ssid': '"' + cell.ssid + '"',
			'key_mgmt': '"NONE"',
		}
	else:
		if cell.encryption_type.startswith('wpa'):
			if len(passkey) != 64:
				passkey = PBKDF2(passkey, cell.ssid, 4096).hexread(32)

			return {
				'ssid': '"' + cell.ssid + '"',
				'psk': passkey,
			}
		elif cell.encryption_type == 'wep':
			# Pass key lengths in bytes for WEP depend on type of key and key length:
			#
			#       64bit   128bit   152bit   256bit
			# hex     10      26       32       58
			# ASCII    5      13       16       29
			#
			# (source: https://en.wikipedia.org/wiki/Wired_Equivalent_Privacy)
			#
			# ASCII keys need to be prefixed with an s: in the interfaces file in order to work with linux' wireless
			# tools

			ascii_lengths = (5, 13, 16, 29)
			if len(passkey) in ascii_lengths:
				# we got an ASCII passkey here (otherwise the key length wouldn't match), we'll need to prefix that
				# with s: in our config for the wireless tools to pick it up properly
				passkey = "s:" + passkey

			return {
				'ssid': '"' + cell.ssid + '"',
				'key': '"' + passkey + '"',
			}
		else:
			raise NotImplementedError


class SchemeWPA(Scheme):
	interfaces = "/etc/wpa_supplicant/wpa_supplicant.conf"

	def __init__(self, interface, name, options=None):
		self.interface = interface
		self.name = name
		self.options = options or {}

	def __str__(self):
		"""
		Returns the representation of a scheme that you would need
		in the /etc/wpa_supplicant/wpa_supplicant.conf file.
		"""

		options = ''.join("\n    {k}={v}".format(k=k, v=v) for k, v in self.options.items())
		return "network={" + options + '\n}\n'

	def __repr__(self):
			return 'Scheme(interface={interface!r}, name={name!r}, options={options!r}'.format(**vars(self))

	def save(self):
		"""
		Writes the configuration to the :attr:`interfaces` file.
		"""
		if not self.find(self.interface, self.name):
			with open(self.interfaces, 'a') as f:
				f.write('\n')
				f.write(str(self))

	@classmethod
	def for_cell(cls, interface, name, cell, passkey=None):
		"""
		Intuits the configuration needed for a specific
		:class:`Cell` and creates a :class:`Scheme` for it.
		"""
		return cls(interface, name, configuration(cell, passkey))

	@classmethod
	def all(cls):
		"""
		Returns an generator of saved schemes.
		"""
		ensure_file_exists(cls.interfaces)
		with open(cls.interfaces, 'r') as f:
			return extract_schemes(f.read(), scheme_class=cls)

	def activate(self):
		"""
		Connects to the network as configured in this scheme.
		"""

		# subprocess.call(['/sbin/ifconfig', self.interface, 'down'])
		# subprocess.call(['service', 'wpa_supplicant', 'restart'])
		# subprocess.call(['/sbin/ifconfig', self.interface, 'up'])
		# subprocess.call(['/sbin/ifconfig', self.interface])
		output = subprocess.check_output(['/sbin/wpa_cli', '-i', self.interface, 'reconfigure'])

		if output.strip() != b'OK':
			raise ConnectionError("Error reconfiguring wpa_supplicant. This is really bad! Check the {} file.".format(SchemeWPA.interfaces))

		tries = 0
		while netifaces.AF_INET not in netifaces.ifaddresses(self.interface):
			tries += 1
			if tries > 20:
				raise ConnectionError("Interface {} did not get an IP address".format(self.interface))
			sleep(0.5)

		return netifaces.ifaddresses(self.interface)[netifaces.AF_INET][0]['addr']


	def delete(self):
		"""
		Deletes the configuration from the /etc/wpa_supplicant/wpa_supplicant.conf file.
		"""
		content = ''
		with open(self.interfaces, 'r') as f:
			lines=f.read().splitlines()
			while lines:
				line=lines.pop(0)

				if line.startswith('#') or not line:
					content+=line+"\n"
					continue

				match = scheme_re.match(line)
				if match:
					options = {}
					ssid=None
					content2=line+"\n"
					while lines and lines[0].startswith(' '):
						line=lines.pop(0)
						content2+=line+"\n"
						key, value = re.sub(r'\s{2,}', ' ', line.strip()).split('=', 1)
						#remove any surrounding quotes on value
						if value.startswith('"') and value.endswith('"'):
							value = value[1:-1]
						#store key, value
						options[key] = value
						#check for ssid (scheme name)
						if key=="ssid":
							ssid=value
					#get closing brace
					line=lines.pop(0)
					content2+=line+"\n"

					#exit if the ssid was not found so just add to content
					if not ssid:
						content+=content2
						continue
					#if this isn't the ssid then just add to content
					if ssid!=self.name:
						content+=content2

				else:
					#no match so add content
					content+=line+"\n"
					continue

		#Write the new content
		with open(self.interfaces, 'w') as f:
			f.write(content)

scheme_re = re.compile(r'network={\s?')


#override extract schemes
def extract_schemes(interfaces, scheme_class=SchemeWPA):
	lines = interfaces.splitlines()
	while lines:
		line = lines.pop(0)
		if line.startswith('#') or not line:
			continue

		match = scheme_re.match(line)
		if match:
			options = {}
			interface="wlan0"
			ssid=None

			while lines and lines[0].startswith(' '):
				key, value = re.sub(r'\s{2,}', ' ', lines.pop(0).strip()).split('=', 1)
				#remove any surrounding quotes on value
				if value.startswith('"') and value.endswith('"'):
					value = value[1:-1]
				#store key, value
				options[key] = value
				#check for ssid (scheme name)
				if key=="ssid":
					ssid=value

			#exit if the ssid was not found
			if ssid is None:
				continue
			#create a new class with this info
			scheme = scheme_class(interface, ssid, options)

			yield scheme
