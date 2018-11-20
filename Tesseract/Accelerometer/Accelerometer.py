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
		radians = math.atan2(y, self.dist(x,z))
		return math.degrees(radians)

	def get_x_rotation(self, x, y, z):
		radians = math.atan2(x, self.dist(y,z))
		return math.degrees(radians)

	def wait_for_movement(self):
		tilting_right = False
		tilting_left = False
		moved_up = False
		last_x = self.read_word_2c(0x3d) / 16384.0
		last_y = - self.read_word_2c(0x3b) / 16384.0
		last_z = self.read_word_2c(0x3f) / 16384.0
		accumulated_movement = 0

		updown_state = 0

		while True:
			#gyro_xout = read_word_2c(0x43)
			#gyro_yout = read_word_2c(0x45)
			#gyro_zout = read_word_2c(0x47)

			gravity = 16384.0

			x = self.read_word_2c(0x3b)
			y = self.read_word_2c(0x3d)
			z = self.read_word_2c(0x3f)
			x_normalized = x / gravity
			y_normalized = y / gravity
			z_normalized = z / gravity

			axis_movement = 10000

			''' UP-DOWN DETECTION '''
			if z < axis_movement and x < axis_movement:
				if updown_state == 0 and y > (gravity + axis_movement):
					print('subindo! y = ' + str(y))
					updown_state = 1

				elif updown_state == 1 and y < (gravity - axis_movement):
					print('parou de subir! y = ' + str(y))
					updown_state = 2

				elif updown_state == 2 and ((gravity - axis_movement) < y < (gravity + axis_movement)):
					print('aguardando! y = ' + str(y))
					updown_state = 3

				elif updown_state == 3 and y < (gravity - axis_movement):
					print('descendo! y = ' + str(y))
					return AccReading.UP_DOWN

			else:
				updown_state = 0

			''' ROTATION DETECTION '''
			x_rotation = self.get_x_rotation(x, y, z)
			if tilting_right or tilting_left:
				if 10 > x_rotation > -10:
					if tilting_right:
						return AccReading.INC_RIGHT
					elif tilting_left:
						return AccReading.INC_LEFT
					else:
						return AccReading.NONE

			elif x_rotation > 70:
				tilting_right = True
				print('Esquerda!')

			elif x_rotation < -70:
				tilting_left = True
				print('Direita!')

			# FIXME More magic numbers, the > 10 and > 150 below. Needs adjusting.
			# FIXME Triggers if there is a simple 1 axis movement, should probably check if at least two axis moved
			#       more than a certain amount
			# If moved sufficiently, add to accumulated_movement
			# If not, reset accumulated_movement
			'''moved = abs(x - last_x) + abs(y - last_y) + abs(z - last_z)
			accumulated_movement += moved if moved > 10 else 0

			if accumulated_movement > 150:
				return AccReading.AGITATION'''