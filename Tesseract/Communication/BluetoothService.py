import bluetooth
import json
import threading
import multiprocessing
import re
import netifaces as ni
from os import popen
from wifi import Cell
from Communication.scheme_wpa import SchemeWPA


class BluetoothService(multiprocessing.Process):
	UUID = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

	def __init__(self, tesseract):
		super().__init__()
		self.tesseract = tesseract

		# Creates socket to listen for bluetooth connections
		self.blue_sck = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
		self.blue_sck.bind(("", bluetooth.PORT_ANY))
		
		self._stop_service = False

	def stop(self):
		print("Shutting down Bluetooth Server")
		try:
			self.blue_sck.shutdown(2)  # Shutdown both listen and send
			self.blue_sck.close()
		except Exception:
			import traceback
			print("An exception happened while closing the bluetooth socket, don't care, here's the traceback:")
			traceback.print_exc()

	def run(self):
		self.blue_sck.listen(1)
		# Announces the service
		bluetooth.advertise_service(self.blue_sck, "Tesseract Server", service_id=self.UUID,
		                            service_classes=[self.UUID, bluetooth.SERIAL_PORT_CLASS], profiles=[bluetooth.SERIAL_PORT_PROFILE])

		try:
			while not self._stop_service:
				print('Waiting for bluetooth connection')
				client_phone_sock, client_phone_info = self.blue_sck.accept()
				print('Device paired!')
				threading.Thread(target=self.answer_client, args=(client_phone_sock,)).start()

		except IOError:
			print('Bluetooth service error.')
			pass

	def answer_client(self, conn):
		msg = json.loads(conn.recv(4096).decode('utf-8'))

		# ============ Processing wifi messages ============= #
		if msg["type"] == "wifi":
			if msg["subtype"] == "request-list":
				conn.send(self.json_all_wifis())

			elif msg["subtype"] == "connect":
				conn.send(self.connect_wifi(msg["value"]))

			elif msg["subtype"] == "request-status":
				conn.send(self.wifi_status())

		# ============ Processing spotify messages ============= #
		elif msg["type"] == "spotify":
			if msg["subtype"] == "connect":
				self.connect_spotify(msg["value"])

			elif msg["subtype"] == "disconnect":
				self.tesseract.is_spotify = False

	def json_all_wifis(self):
		json_list = {"type": "wifi", "subtype": "list", "value": []}
		for cell in list(Cell.all('wlan0')):
			json_list.get("value").append({"ssid": cell.ssid, "signal": cell.signal, "encryption_type": cell.encryption_type})
		return json.dumps(json_list, separators=(',', ':')).encode('utf-8')

	def connect_spotify(self, value):
		json_value = json.loads(json.loads(value))
		self.tesseract.spotify.token = json_value["token"]
		print('teste: ' + self.tesseract.spotify.token)
		self.tesseract.is_spotify = True

	def connect_wifi(self, value):
		json_list = {"type": "wifi", "subtype": "return", "value": {"success": False, "addr": None}}

		scheme = None

		for cell in list(Cell.all('wlan0')):
			if cell.ssid == value['ssid']:

				# Check if there is already a scheme saved with that ssid
				# If there is, delete it to make sure we update the password
				# It honestly takes longer to regenerate the password and check if it is the same than to just regenerate the entire scheme
				for s in list(SchemeWPA.all()):
					if s.options['ssid'] == value['ssid']:
						s.delete()

				scheme = SchemeWPA.for_cell('wlan0', cell.ssid, cell, value['psk'])
				scheme.save()

				try:
					addr = scheme.activate()
					json_list.get("value").update({"success": True, "addr": addr})
				except ConnectionError:
					scheme.delete()
					scheme = None

				break

		return json.dumps(json_list, separators=(',', ':')).encode('utf-8')

	def wifi_status(self):
		json_list = {"type": "wifi", "subtype": "status", "value": {"connected": False, "ssid": None, "addr": None}}

		match = re.search('^wlan0\s*ESSID:"(.+)"$', popen("iwgetid").read(), re.MULTILINE)
		if ni.AF_INET in ni.ifaddresses('wlan0') and match:
			json_list.get("value").update({"connected": True, "addr": ni.ifaddresses('wlan0')[ni.AF_INET][0]['addr'], "ssid": match.group(1)})

		return json.dumps(json_list, separators=(',', ':')).encode('utf-8')
