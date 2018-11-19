from Accelerometer.AccReading import AccReading
import smbus
import math


class Accelerometer():

	def __init__(self):
		self.bus = smbus.SMBus(1) # or bus = smbus.SMBus(1) for Revision 2 boards
		self.address = 0x68       # This is the address value read via the i2cdetect command
		# Power management registers
		self.power_mgmt_1 = 0x6b
		self.power_mgmt_2 = 0x6c
		self.bus.write_byte_data(self.address, self.power_mgmt_1, 0)


	def read_byte(self, adr):
		return self.bus.read_byte_data(self.address, adr)


	def read_word(self, adr):
		high = self.bus.read_byte_data(self.address, adr)
		low = self.bus.read_byte_data(self.address, adr+1)
		val = (high << 8) + low
		return val


	def read_word_2c(self, adr):
		val = self.read_word(adr)
		if (val >= 0x8000):
			return -((65535 - val) + 1)
		else:
			return val


	def dist(self, a, b):
		return math.sqrt((a*a)+(b*b))


	def get_y_rotation(self, x, y, z):
		radians = math.atan2(x, self.dist(y,z))
		return -math.degrees(radians)


	def get_x_rotation(self, x, y, z):
		radians = math.atan2(y, self.dist(x,z))
		return math.degrees(radians)


	def wait_for_movement(self):
		tilting_right = False
		tilting_left = False
		moved_up = False
		last_x = self.read_word_2c(0x3d) / 16384.0
		last_y = - self.read_word_2c(0x3b) / 16384.0
		last_z = self.read_word_2c(0x3f) / 16384.0
		accumulated_movement = 0

		while True:
			#gyro_xout = read_word_2c(0x43)
			#gyro_yout = read_word_2c(0x45)
			#gyro_zout = read_word_2c(0x47)

			# Accelerometer is sideways, rotated 90° to the right
			# code_x = real_y
			# code_y = - real_x



			x = self.read_word_2c(0x3d) / 16384.0
			y = - self.read_word_2c(0x3b) / 16384.0
			z = self.read_word_2c(0x3f) / 16384.0

			# FIXME More magic numbers, the > 10 and > 150 below. Needs adjusting.
			# FIXME Triggers if there is a simple 1 axis movement, should probably check if at least two axis moved
			#       more than a certain amount
			# If moved sufficiently, add to accumulated_movement
			# If not, reset accumulated_movement
			moved = abs(x - last_x) + abs(y - last_y) + abs(z - last_z)
			accumulated_movement += moved if moved > 10 else 0

			if accumulated_movement > 150:
				return AccReading.AGITATION

			x_rotation = self.get_x_rotation(x, y, z)
			y_rotation = self.get_y_rotation(x, y, z)

			if tilting_right or tilting_left:
				if x_rotation < 10 and x_rotation > -10:
					if tilting_right:
						return AccReading.INC_RIGHT
					elif tilting_left:
						return AccReading.INC_LEFT
					else:
						return AccReading.NONE

			elif moved_up:
				# FIXME 15 is a magic number, don't know precise reading, so guessing slightly smaller than number below
				if y < last_y - 15:
					return AccReading.UP_DOWN

			# FIXME 20 is a magic number, don't know precise reading, so guessing
			elif y > last_y + 20:
				moved_up = True
				print('Cima!')

			elif x_rotation > 70:
				tilting_right = True
				print('Direita!')

			elif x_rotation < -70:
				tilting_left = True
				print('Esquerda!')

			# Update 'last' variables for next read
			last_x = x
			last_y = y
			last_z = z


