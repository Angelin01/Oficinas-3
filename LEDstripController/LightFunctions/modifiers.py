from numpy import ceil, floor


_wave_values = [0.0955, 0.3455, 0.6545, 0.9045, 1.0, 0.9045, 0.6545, 0.3455, 0.0955]
_wave_values_len = 11

def wave_modifier(values_sequence):
    """
    Modifier that makes a "wave" like pattern with various colors.
    :param values_sequence: The sequence of colors to update.
    :return: The sequence to use for the LEDs.
    """
    wave_sequence = []
    for step in range(len(values_sequence)):
        n_led = len(values_sequence[step])

        # Pads the available pre calculated sin values with 0s, to keep extra LEDs off
        padded_wave = [0.0]*int(ceil((n_led - _wave_values_len)/2)) + _wave_values + [0.0]*int(floor((n_led - _wave_values_len)/2))
        # Shifts the padded wave according to the number of LEDs and what step we are on at the moment
        padded_wave = padded_wave[-int(floor(step*n_led/len(values_sequence))):] + padded_wave[:-int(ceil(step*n_led/len(values_sequence)))]

        # Multiplies each color value in the sequence for the intensity of the wave and returns the array of tuples
        wave_sequence.append([(GRB[0]*value, GRB[1]*value, GRB[2]*value) for value, GRB in zip(padded_wave,values_sequence[step])])
    return wave_sequence
