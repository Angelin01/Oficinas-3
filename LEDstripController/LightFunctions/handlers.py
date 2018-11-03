from controllerUtils import rotate_list_left


def standard_handler(args_dict: dict):
    """
    Handles the update of a standard pre computed function.
    :param args_dict: Created by the 'create_standard_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    step = args_dict['step']

    # Updating step for the next iteration.
    args_dict['step'] = (step + 1) % args_dict['sequence_len']

    return color_sequence[step]


def stream_handler(args_dict: dict):
    """
    The stream effect has the colors of the LEDs move like a stream, where the color of a LEDs is given to its
    neighbor in the next update step.
    :param args_dict: Created by the 'create_standard_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    color_seq_len = args_dict['color_seq_len']
    color_itr = args_dict['color_itr']
    n_leds = args_dict['n_leds']

    step_sequence = [color_sequence[c % color_seq_len] for c in range(color_itr, n_leds + color_itr)]

    # Updating step for the next iteration.
    args_dict['color_itr'] = (color_itr + 1) % color_seq_len

    return step_sequence


def wave_handler(args_dict: dict):
    """
    Handles the update of a wave function.
    :param args_dict: Created by the 'create_wave_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    wave = args_dict['wave']
    color_delay = args_dict['color_delay']
    n_leds = args_dict['n_leds']
    delay_itr = args_dict['delay_itr']
    color_itr = args_dict['color_itr']

    step_sequence = []

    color_seq_len = args_dict['color_seq_len']
    wave_len = args_dict['wave_len']

    for l in range(n_leds):

        step_sequence.append(
            [int(color_sequence[color_itr][channel] * wave[l % wave_len]) for channel in range(3)])

        delay_itr += 1

        if delay_itr % color_delay == 0:
            delay_itr = 0
            color_itr += 1
            if color_itr == color_seq_len:
                color_itr = 0

    # Updating wave for next iteration.
    rotate_list_left(wave)

    args_dict['delay_itr'] = delay_itr
    args_dict['color_itr'] = color_itr

    return step_sequence


def breathe_handler(args_dict: dict):
    """
    The breathe effect is comprised of all LEDs changing intensity in a sinusoidal manner, imitating an
    inflating balloon.
    :param args_dict: Created by the 'create_wave_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    wave = args_dict['wave']
    color_delay = args_dict['color_delay']
    n_leds = args_dict['n_leds']
    delay_itr = args_dict['delay_itr']
    color_itr = args_dict['color_itr']

    color_seq_len = args_dict['color_seq_len']

    step_sequence = [int(color_sequence[color_itr][channel] * wave[0]) for channel in range(3)] * n_leds

    delay_itr += 1

    if delay_itr % color_delay == 0:
        delay_itr = 0
        color_itr += 1
        if color_itr == color_seq_len:
            color_itr = 0

    # Updating wave for next iteration.
    rotate_list_left(wave)

    args_dict['delay_itr'] = delay_itr
    args_dict['color_itr'] = color_itr

    return step_sequence
