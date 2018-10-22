_rising_ring_map = [2, 3, 1, 4, 0, 5, 23, 6, 22, 7, 21, 8, 20, 9, 19, 10, 18, 11, 17, 12, 16, 13, 15, 14]
_descending_ring_map = [14, 15, 13, 16, 12, 17, 11, 18, 10, 19, 9, 20, 8, 21, 7, 22, 6, 23, 5, 0, 4, 1, 3, 2]
_ltor_ring_map = [20, 21, 19, 22, 18, 23, 17, 0, 16, 1, 15, 2, 14, 3, 13, 4, 12, 5, 11, 6, 10, 7, 9, 8]
_rtol_ring_map = [8, 9, 7, 10, 6, 11, 5, 12, 4, 13, 3, 14, 2, 15, 1, 16, 0, 17, 23, 18, 22, 19, 21, 20]


def rising_ring(values_sequence_step):
	"""
	Fast mapping of values from a values sequence step to a rising ring pattern
	Maps only a step since FFT values come one step at a time
	Allows for use with the map() function for multiple pre generated steps
	WARNING: SKIPS CHECKS ON LENGTH AND OTHER SANITY TESTS, IS INTENDED TO BE FAST
	:param values_sequence_step: The step of the sequence of values to convert
	:return: the properly mapped values sequence step
	"""
	return [x for _, x in sorted(zip(_rising_ring_map, values_sequence_step))]


def descending_ring(values_sequence_step):
	"""
	Fast mapping of values from a values sequence step to a descending ring pattern
	Maps only a step since FFT values come one step at a time
	Allows for use with the map() function for multiple pre generated steps
	WARNING: SKIPS CHECKS ON LENGTH AND OTHER SANITY TESTS, IS INTENDED TO BE FAST
	:param values_sequence_step: The step of the sequence of values to convert
	:return: the properly mapped values sequence step
	"""
	return [x for _, x in sorted(zip(_descending_ring_map, values_sequence_step))]


def ltor_ring(values_sequence_step):
	"""
	Fast mapping of values from a values sequence step to a left to right ring pattern
	Maps only a step since FFT values come one step at a time
	Allows for use with the map() function for multiple pre generated steps
	WARNING: SKIPS CHECKS ON LENGTH AND OTHER SANITY TESTS, IS INTENDED TO BE FAST
	:param values_sequence_step: The step of the sequence of values to convert
	:return: the properly mapped values sequence step
	"""
	return [x for _, x in sorted(zip(_ltor_ring_map, values_sequence_step))]


def rtol_ring(values_sequence_step):
	"""
	Fast mapping of values from a values sequence step to a right to left ring pattern
	Maps only a step since FFT values come one step at a time
	Allows for use with the map() function for multiple pre generated steps
	WARNING: SKIPS CHECKS ON LENGTH AND OTHER SANITY TESTS, IS INTENDED TO BE FAST
	:param values_sequence_step: The step of the sequence of values to convert
	:return: the properly mapped values sequence step
	"""
	return [x for _, x in sorted(zip(_rtol_ring_map, values_sequence_step))]
