from math import ceil, floor


_wave_values = [0.0955, 0.3455, 0.6545, 0.9045, 1.0, 0.9045, 0.6545, 0.3455, 0.0955]
_wave_values_len = 11


def standard_handler(values_sequence, step):
    """
    Simple handler, just returns the sequence for the specific step
    :param values_sequence: a johnson_gradient value sequence.
    :param step: Which of the values to return in the sequence.
    :return: The sequence to use for the LEDs.
    """
    return values_sequence[step]


def wave_handler(values_sequence, step):
    """
    Handler that makes a "wave" like pattern with various colors.
    :param values_sequence: The sequence of colors to update.
    :param step: The current step in the sequence.
    :return: The sequence to use for the LEDs.
    """
    n_led = len(values_sequence[step])

    # Pads the available pre calculated sin values with 0s, to keep extra LEDs off
    padded_wave = [0.0]*ceil((n_led - _wave_values_len)/2) + _wave_values + [0.0]*floor(ceil((n_led - _wave_values_len)/2))
    # Shifts the padded wave according to the number of LEDs and what step we are on at the moment
    padded_wave = padded_wave[-floor(n_led/values_sequence):] + padded_wave[:-floor(n_led/values_sequence)]

    # Multiplies each color value in the sequence for the intensity of the wave and returns the array of tuples
    return [(g*value, r*value, b*value) for value in padded_wave for g, r, b in values_sequence[step]]