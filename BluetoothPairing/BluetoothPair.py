#!/usr/bin/python

from __future__ import absolute_import, print_function, unicode_literals

from optparse import OptionParser

import dbus
import dbus.mainloop.glib
import dbus.service

import Agent

try:
	from gi.repository import GObject
except ImportError:
	import gobject as GObject
import bluezutils


class BluetoothPair():
	BUS_NAME = 'org.bluez'
	AGENT_PATH = "/test/agent"
	AGENT_CAPABILITY = 'NoInputNoOutput'

	bus = None
	device_obj = None
	dev_path = None

	def __init__(self, from_acc_queue=None, display_queue=None):
		super().__init__()
		# self.from_acc_queue = from_acc_queue
		# self.display_queue = display_queue
		self.__configure_pair_agent()

	def __dev_connect(self, path):
		dev = dbus.Interface(self.bus.get_object("org.bluez", path), "org.bluez.Device1")
		dev.Connect()

	def __pair_reply(self):
		print("Device paired")
		self.agent.__set_trusted(self.dev_path)
		self.__dev_connect(self.dev_path)
		self.mainloop.quit()

	def __pair_error(self, error):
		err_name = error.get_dbus_name()
		if err_name == "org.freedesktop.DBus.Error.NoReply" and self.device_obj:
			print("Timed out. Cancelling pairing")
			self.device_obj.CancelPairing()
		else:
			print("Creating device failed: %s" % error)

		self.mainloop.quit()

	def __configure_pair_agent(self):
		print("Configuring agent pair")
		dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

		bus = dbus.SystemBus()

		parser = OptionParser()
		parser.add_option("-i", "--adapter", action="store", type="string", dest="adapter_pattern", default=None)
		parser.add_option("-c", "--capability", action="store", type="string", dest="capability")
		parser.add_option("-t", "--timeout", action="store", type="int", dest="timeout", default=60000)
		(options, args) = parser.parse_args()

		capability = self.AGENT_CAPABILITY
		if options.capability:
			capability = options.capability

		self.mainloop = GObject.MainLoop()

		path = "/test/agent"
		self.agent = Agent.Agent(bus, path, self.mainloop)

		obj = bus.get_object(self.BUS_NAME, "/org/bluez")
		manager = dbus.Interface(obj, "org.bluez.AgentManager1")
		manager.RegisterAgent(path, capability)

		print("Agent registered")

		# Fix-up old style invocation (BlueZ 4)
		if len(args) > 0 and args[0].startswith("hci"):
			options.adapter_pattern = args[0]
			del args[:1]

		if len(args) > 0:
			device = bluezutils.find_device(args[0], options.adapter_pattern)
			self.dev_path = device.object_path
			self.agent.set_exit_on_release(False)
			device.Pair(reply_handler=self.__pair_reply, error_handler=self.__pair_error, timeout=60000)
			self.device_obj = device
		else:
			manager.RequestDefaultAgent(path)

	def run(self):
		self.mainloop.run()

	def stop(self):
		self.mainloop.quit()