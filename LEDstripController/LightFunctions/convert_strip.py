
_rising_ring_map = [2, 1, 3, 0, 4, 19, 5, 18, 6, 17, 7, 16, 8, 15, 9, 14, 10, 13, 11, 12]
_descending_ring_map = [12, 11, 13, 10, 14, 9, 15, 8, 16, 7, 17, 6, 18, 5, 19, 4, 0, 3, 1, 2]
_ltor_ring_map = [7, 6, 8, 5, 9, 4, 10, 3, 11, 2, 12, 1, 13, 0, 14, 19, 15, 16, 18, 17]
_rtol_ring_map = [17, 18, 16, 15, 19, 14, 0, 13, 1, 12, 2, 11, 3, 10, 4, 9, 5, 8, 6, 7]


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
