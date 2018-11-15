

def create_wave_handler_args(color_sequence, wave, color_delay, n_leds):
    """
    :param color_sequence: A list containing GRB values.
    :param wave: A list containing normalised values.
    :param color_delay: The number of updates it take to change the color.
    :param n_leds: The number of LEDs being controlled.
    :return: The arguments to be used by the controller.
    """

    handler_args = {
        'color_sequence': color_sequence,
        'color_seq_len': len(color_sequence),
        'wave': wave,
        'wave_len': len(wave),
        'color_delay': color_delay,
        'n_leds': n_leds,
        'delay_itr': 0,
        'color_itr': 0
    }

    return handler_args


def create_standard_handler_args(state_sequence):
    """
    :param state_sequence: A sequence of pre computed states ready to be taken by the controller.
    :return: The arguments to be used by the controller.
    """

    handler_args = {
        'sequence': state_sequence,
        'sequence_len': len(state_sequence),
        'step': 0
    }

    return handler_args


def create_breathe_handler_args(color_sequence, wave, color_delay, n_leds):
    """
    :param color_sequence: A list containing GRB values.
    :param wave: A list containing normalised values.
    :param color_delay: The number of updates it take to change the color.
    :param n_leds: The number of LEDs being controlled.
    :return: The arguments to be used by the controller.
    """

    handler_args = {
        'color_sequence': color_sequence,
        'color_seq_len': len(color_sequence),
        'wave': wave,
        'color_delay': color_delay,
        'n_leds': n_leds,
        'delay_itr': 0,
        'color_itr': 0
    }

    return handler_args


def create_stream_handler_args(color_sequence, n_leds):
    """
    :param color_sequence: A list containing GRB values.
    :param n_leds: The number of LEDs being controlled.
    :return: The arguments to be used by the controller.
    """

    handler_args = {
        'color_sequence': color_sequence,
        'color_seq_len': len(color_sequence),
        'n_leds': n_leds,
        'color_itr': 0
    }

    return handler_args


def create_fft_freq_color_handler_args(min_color: tuple, max_color: tuple, resolution: int):

    handler_args = {
        'min_color': min_color,
        'max_color': max_color,
        'resolution': 20 // resolution,
        'max_fft_sample': 1
    }

    return handler_args
