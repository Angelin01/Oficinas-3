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
		inclinado_direita = False
		inclinado_esquerda = False

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
			x_rotation = self.get_x_rotation(x, y, z)
			y_rotation = self.get_y_rotation(x, y, z)

			if inclinado_direita or inclinado_esquerda:
				if x_rotation < 10 and x_rotation > -10:
					if inclinado_direita:
						return AccReading.INC_RIGHT
					elif inclinado_esquerda:
						return AccReading.INC_LEFT
					else:
						return AccReading.NONE
			elif x_rotation > 70:
				inclinado_direita = True
				print('Direita!')
			elif x_rotation < -70:
				inclinado_esquerda = True
				print('Esquerda!')
