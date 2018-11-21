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

	def __init__(self, tesseract, main_queue, leds_queue, acc_queue, display_queue, from_acc_queue):
		super().__init__()
		self.main_queue = main_queue
		self.leds_queue = leds_queue
		self.acc_queue = acc_queue
		self.tesseract = tesseract
		self.display_queue = display_queue
		self.from_acc_queue = from_acc_queue

		# Creates socket to listen for bluetooth connections
		self.blue_sck = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
		self.blue_sck.bind(("", bluetooth.PORT_ANY))

		self._stop_service = False

	def stop_service(self):
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
				self.display_queue.put(['Esperando con.', 'Bluetooth...'])
				client_phone_sock, client_phone_info = self.blue_sck.accept()
				print('Device paired!')
				self.display_queue.put(['Dispositivo', 'conectado!'])
				threading.Thread(target=self.read_queue_from_acc, args=(client_phone_sock,)).start()
				threading.Thread(target=self.answer_client, args=(client_phone_sock,)).start()

		except IOError:
			print('Bluetooth service error.')
			pass

	def read_queue_from_acc(self, conn):
		while True:
			try:
				spotify_command = self.from_acc_queue.get()

				if spotify_command["type"] == "spotify":
					print('sending command to app')

					if spotify_command["subtype"] == "command":
						print('command: ' + spotify_command["value"])
						self.bluetooth_send(conn, json.dumps(spotify_command, separators=(',', ':')).encode('utf-8'))
			except:
				pass

	def answer_client(self, conn):
		while True:
			try:
				msg = json.loads(conn.recv(4096).decode('utf-8'))

				# ============ Processing wifi messages ============= #
				if msg["type"] == "wifi":
					print('wifi command')
					if msg["subtype"] == "request-list":
						available_wifis = self.json_all_wifis()
						self.bluetooth_send(conn, available_wifis)

					elif msg["subtype"] == "connect":
						connect_wifi_attempt = self.connect_wifi(msg["value"])
						self.bluetooth_send(conn, connect_wifi_attempt)

					elif msg["subtype"] == "request-status":
						wifi_status = self.wifi_status()
						self.bluetooth_send(conn, wifi_status)

				# ============ Processing spotify messages ============= #
				elif msg["type"] == "spotify":
					print('spotify command')
					self.acc_queue.put(msg)
			except:
				pass

	def bluetooth_send(self, conn, wifi_status):
		msg = wifi_status + b'--end_of_message'
		sent = conn.send(msg)



	@staticmethod
	def json_all_wifis():
		json_list = {"type": "wifi", "subtype": "list", "value": []}
		for cell in list(Cell.all('wlan0')):
			json_list.get("value").append({"ssid": cell.ssid, "signal": cell.signal, "encryption_type": cell.encryption_type})
		return json.dumps(json_list, separators=(',', ':')).encode('utf-8')

	@staticmethod
	def connect_wifi(value):
		json_list = {"type": "wifi", "subtype": "return", "value": {"success": False, "addr": None}}

		for cell in list(Cell.all('wlan0')):
			if cell.ssid == value['ssid']:
				print('found ssid ' + value['ssid'])

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

				break

		return json.dumps(json_list, separators=(',', ':')).encode('utf-8')

	@staticmethod
	def wifi_status():
		json_list = {"type": "wifi", "subtype": "status", "value": {"connected": False, "ssid": None, "addr": None}}

		match = re.search('^wlan0\s*ESSID:"(.+)"$', popen("iwgetid").read(), re.MULTILINE)
		if ni.AF_INET in ni.ifaddresses('wlan0') and match:
			json_list.get("value").update({"connected": True, "addr": ni.ifaddresses('wlan0')[ni.AF_INET][0]['addr'], "ssid": match.group(1)})

		return json.dumps(json_list, separators=(',', ':')).encode('utf-8')
