import dbus


class Rejected(dbus.DBusException):
	_dbus_error_name = "org.bluez.Error.Rejected"
