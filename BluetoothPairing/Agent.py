import dbus
import dbus.service

from Rejected import Rejected

BLUETOOTH_AGENT = 'bluetooth_agent'
PAIR_AUTHORIZATION = "authorization"


class Agent(dbus.service.Object):
	exit_on_release = True

	AGENT_INTERFACE = 'org.bluez.Agent1'

	def __init__(self, bus, path, mainloop, from_acc_queue=None, display_queue=None):
		super().__init__(bus, path)

		self.mainloop = mainloop
		self.from_acc_queue = from_acc_queue
		self.display_queue = display_queue
		self.bus = bus
		print("Init bluetooth agent")

	def set_exit_on_release(self, exit_on_release):
		self.exit_on_release = exit_on_release

	@dbus.service.method(AGENT_INTERFACE, in_signature="", out_signature="")
	def Release(self):
		print("Release")
		if self.exit_on_release:
			self.mainloop.quit()

	@dbus.service.method(AGENT_INTERFACE, in_signature="os", out_signature="")
	def AuthorizeService(self, device, uuid):
		print("AuthorizeService (%s, %s)" % (device, uuid))
		# authorize = self.__ask("Authorize connection (yes/no): ")
		authorize = "yes"
		if authorize == "yes":
			return
		raise Rejected("Connection rejected by user")

	@dbus.service.method(AGENT_INTERFACE, in_signature="o", out_signature="s")
	def RequestPinCode(self, device):
		print("RequestPinCode (%s)" % device)
		self.__set_trusted(device)
		return self.__ask("Enter PIN Code: ")

	@dbus.service.method(AGENT_INTERFACE, in_signature="o", out_signature="u")
	def RequestPasskey(self, device):
		print("RequestPasskey (%s)" % device)
		self.__set_trusted(device)
		passkey = self.__ask("Enter passkey: ")
		return dbus.UInt32(passkey)

	@dbus.service.method(AGENT_INTERFACE, in_signature="ouq", out_signature="")
	def DisplayPasskey(self, device, passkey, entered):
		print("DisplayPasskey (%s, %06u entered %u)" % (device, passkey, entered))

	@dbus.service.method(AGENT_INTERFACE, in_signature="os", out_signature="")
	def DisplayPinCode(self, device, pincode):
		print("DisplayPinCode (%s, %s)" % (device, pincode))

	@dbus.service.method(AGENT_INTERFACE, in_signature="ou", out_signature="")
	def RequestConfirmation(self, device, passkey):
		print("Inside request confirmation")
		print("RequestConfirmation (%s, %06d)" % (device, passkey))
		# self.display_queue.put("Confirm passkey (Move Up):")
		# confirm = self.__wait_answer_from_acc()
		confirm="yes"
		if confirm == "yes":
			self.__set_trusted(device)
			return
		print("Passkey Not matched")
		raise Rejected("Passkey doesn't match")

	@dbus.service.method(AGENT_INTERFACE, in_signature="o", out_signature="")
	def RequestAuthorization(self, device):
		return

	@dbus.service.method(AGENT_INTERFACE, in_signature="", out_signature="")
	def Cancel(self):
		print("Cancel")

	def __set_trusted(self, path):
		props = dbus.Interface(self.bus.get_object("org.bluez", path), "org.freedesktop.DBus.Properties")
		props.Set("org.bluez.Device1", "Trusted", True)

	@staticmethod
	def __ask(prompt):
		try:
			return input(prompt)
		except Exception:
			return input(prompt)

	def __wait_answer_from_acc(self):
		while True:
			print("Waiting acc response")
			try:
				answer = self.from_acc_queue.get()
				if self.__is_authorization(answer):
					return answer["type"][PAIR_AUTHORIZATION]
			except:
				pass

	@staticmethod
	def __is_authorization(message):
		if message["type"] != BLUETOOTH_AGENT:
			return False

		if message["subtype"] != PAIR_AUTHORIZATION:
			return False

		return True
