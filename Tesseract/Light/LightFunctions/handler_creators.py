from Light.color import ColorMode, Color


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


def create_standard_handler_args(color_sequence):
    """
    :param color_sequence: A sequence of pre computed states ready to be taken by the controller.
    :return: The arguments to be used by the controller.
    """

    handler_args = {
        'sequence': color_sequence,
        'sequence_len': len(color_sequence),
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


def create_fft_color_handler_args(min_color: tuple, max_color: tuple,
                                  resolution: int, intensity: int, color_mode: ColorMode):

    handler_args = {
        'min_color': min_color,  # RGB or HSL.
        'max_color': max_color,  # RGB or HSL.
        'resolution': resolution,
        'intensity': intensity,
        'color_mode': color_mode
    }

    return handler_args


def create_fft_bars_handler_args(low_color: tuple, mid_color: tuple, high_color: tuple,
                                 max_intensity: int, color_mode=ColorMode.RGB):

    if color_mode == ColorMode.RGB:
        low_color = [c / 255 for c in low_color]
        mid_color = [c / 255 for c in mid_color]
        high_color = [c / 255 for c in high_color]
    else:
        low_color = low_color[0] / 360, low_color[1] / 100, low_color[2] / 100
        mid_color = mid_color[0] / 360, mid_color[1] / 100, mid_color[2] / 100
        high_color = high_color[0] / 360, high_color[1] / 100, high_color[2] / 100

    handler_args = {
        'low_color': Color(*low_color, color_mode=color_mode),
        'mid_color': Color(*mid_color, color_mode=color_mode),
        'high_color': Color(*high_color, color_mode=color_mode),
        'intensity': max_intensity
    }

    return handler_args
