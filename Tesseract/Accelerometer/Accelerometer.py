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
		#region [ Magic numbers ]
		agitation_intensity_threshold = 18000
		agitation_counter_max = 70
		tilting_counter_max = 60
		tilting_angle_detection = 30

		axis_movement = 10000
		gravity = 16384.0
		updown_tolerance = 4000
		#endregion

		tilting_right_count = 0
		tilting_left_count = 0
		agitation_counter = 0
		updown_state = 0

		while True:
			#gyro_xout = read_word_2c(0x43)
			#gyro_yout = read_word_2c(0x45)
			#gyro_zout = read_word_2c(0x47)
			x = self.read_word_2c(0x3b)
			y = self.read_word_2c(0x3d)
			z = self.read_word_2c(0x3f)
			#x_normalized = x / gravity
			#y_normalized = y / gravity
			#z_normalized = z / gravity

			''' UP-DOWN DETECTION '''
			if z < 10000 and x < 10000:
				if updown_state == 0 and y > 20000:
					# going up
					updown_state = 1

				elif updown_state == 1 and y < 10000:
					# going back down
					updown_state = 2

				elif updown_state == 2 and y > 20000:
					# movement complete
					return AccReading.UP_DOWN

			else:
				updown_state = 0

			''' ROTATION DETECTION '''
			x_rotation = self.get_x_rotation(x, y, z)
			if tilting_right_count > tilting_counter_max or tilting_left_count > tilting_counter_max:
				if 10 > x_rotation > -10:
					if tilting_right_count > tilting_counter_max:
						return AccReading.INC_RIGHT
					elif tilting_left_count > tilting_counter_max:
						return AccReading.INC_LEFT
					else:
						return AccReading.NONE

			elif x_rotation > tilting_angle_detection:
				tilting_right_count = 0
				tilting_left_count += 1
				if tilting_right_count > tilting_counter_max:
					print('Esquerda!')

			elif x_rotation < -tilting_angle_detection:
				tilting_right_count += 1
				tilting_left_count = 0
				if tilting_left_count > tilting_counter_max:
					print('Direita!')

			else:
				tilting_right_count = 0
				tilting_left_count = 0

			''' AGITATION DETECTION '''
			if math.sqrt((x*x) + (y*y) + (z*z)) > agitation_intensity_threshold:
				agitation_counter += 1
			else:
				agitation_counter = 0

			# print('agitation counter: ' + str(agitation_counter))

			if agitation_counter == agitation_counter_max:
				return AccReading.AGITATION