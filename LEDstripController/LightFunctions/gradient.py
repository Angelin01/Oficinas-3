from color import Color, ColorMode
from math import ceil, floor


_wave_values = [0.0, 0.3455, 0.6545, 0.9045, 1.0, 1.0, 1.0, 0.9045, 0.6545, 0.3455, 0.0]
_wave_values_len = 11

def gen_hue_gradient(start_hue: int, end_hue: int, speed: float, intensity: int, gradient_backwards: bool = False):
    """
    Generates an iterator to make the transition from start_hue to end_hue with certain speed for n_leds.
    The color wheel is a cycle, so if end_hue is less than start_hue, it will loop.

    :param start_hue: A value between 0 and 360 representing the color wheel.
    :param end_hue: A value between 0 and 360 representing the color wheel.
    :param speed: The speed of the gradient change.
    :param intensity: The intensity value in which the gradient occurs. Between 0 and 255.
    :param gradient_backwards: A bool to say if the gradient goes forward (from start to end if False) or the contrary.
    :return: TODO
    """

    if end_hue - start_hue < 0:
        end_hue += 360

    elif start_hue == end_hue:
        end_hue += 360

    if not gradient_backwards:
        gradient_inc = speed
    else:
        gradient_inc = -speed
        start_hue, end_hue = end_hue, start_hue

    current_hue = start_hue

    hue_steps = []

    if not gradient_backwards:
        while current_hue <= end_hue:
            hue_steps.append(current_hue % 360)
            current_hue += gradient_inc
    else:
        while current_hue >= end_hue:
            hue_steps.append(current_hue % 360)
            current_hue += gradient_inc

    gradient_colors = [Color(hue / 360, 1, 0.5, ColorMode.HSL) for hue in hue_steps]

    Color.batch_hsl_to_rgb(gradient_colors)

    gradient_colors = [gc.get_ws2812_rgb(intensity) for gc in gradient_colors]

    return gradient_colors


def hue_gradient_update_all(values_sequence, step, n_led):
    """
    TODO
    :param values_sequence: TODO
    :param step: TODO
    :param n_led: TODO
    :return: TODO
    """
    return values_sequence[step] * n_led


def wave_gradient_update_all(values_sequence, step, n_led):
    """
    TODO
    :param values_sequence: TODO
    :param step: TODO
    :param n_led: TODO
    :return: TODO
    """
    if n_led < _wave_values_len:
        raise ValueError("Number of LEDs is too small for wave, minimum is {}".format(_wave_values_len))

    # Pads the available pre calculated sin values with 0s, to keep extra LEDs off
    padded_wave = [0]*ceil((n_led - _wave_values_len)/2) + _wave_values + [0]*floor(ceil((n_led - _wave_values_len)/2))
    # Shifts the padded wave according to the number of LEDs and what step we are on at the moment
    padded_wave = padded_wave[-floor(n_led/values_sequence):] + padded_wave[:-floor(n_led/values_sequence)]

    # TODO EXPLAIN THIS CRAZYNESS
    return [(g*value, r*value, b*value) for grb, value in padded_wave for g, r, b in values_sequence[step]]
