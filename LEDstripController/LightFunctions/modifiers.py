import math
from enum import Enum

from controllerUtils import rotate_list_left


def wave_modifier(color_sequence: list, color_delay: int, wave_speed: float, wave_count: float, n_leds: int):
    """
    :param color_sequence: The color sequence to be updated in a wave manner.
    :param color_delay: The delay between color_changes.
    :param wave_speed: The speed in which the wave moves along the LED strip.
    :param wave_count: The number of waves to be present in the strip containing n_leds.
    :param n_leds: The number of LEDs being controlled.
    :return: A sequence of color stages ready to be run by the controller.
    """

    wave = gen_wave(wave_speed, wave_count, n_leds)

    return_sequence = []

    len_color_seq = len(color_sequence)
    delay_itr = 0
    color_itr = 0

    color_looped = False

    while color_looped is False:

        # number of wave steps (the wave movement)
        for w in range(len(wave)):
            modified_sequence = []

            # Iterating through the values in the wave list and updating the input sequence.
            for w_i in range(len(wave)):

                if w_i >= n_leds:
                    break

                modified_sequence.append(
                    [int(color_sequence[color_itr % len_color_seq][channel] * wave[w_i]) for channel in range(3)])

                delay_itr += 1

                if delay_itr % color_delay == 0:

                    delay_itr = 0
                    color_itr += 1

                    if color_itr == len_color_seq:
                        color_looped = True

            rotate_list_left(wave)
            return_sequence.append(modified_sequence[:n_leds])

    return return_sequence


def gen_wave(wave_speed: float, wave_count: float, n_leds: int):
    """
    :param wave_speed: The speed in which the wave moves along the LED strip.
    :param wave_count: The number of waves to be present in the strip containing n_leds.
    :param n_leds: The number of LEDs to be controlled.
    :return: A sequence of values between 0 and 1 in a sinusoidal pattern.
    """
    wave = []
    t = 0

    if wave_speed == 0:
        raise ValueError("Wave speed cannot be zero.")

    wave_limit = math.pi * 2
    wave_speed = math.pi * 2 / (n_leds * 1 / wave_speed / wave_count)

    # Generating wave.
    while t <= wave_limit:
        wave.append(0.5 + 0.5 * math.sin(t * wave_count))
        t += wave_speed

    return wave


class JohnsonType(Enum):
    FULL_RING = 0
    BEAT = 1
    REACH = 2


def johnson_modifier(color_values: list, n_leds: int, johnson_type: JohnsonType = JohnsonType.FULL_RING):
    """
    Uses, so that instead of changing colors the LEDs turn on/off
    sequentially
    :param color_values: The sequence of colors.
    :param n_leds: The number of LEDs to be controlled.
    :param johnson_type: If JohnsonType.FULL_RING, the sequence will first turn on LEDs on then repeat,
                            but then turning them off.
                         If JohnsonType.BEAT, the sequence will first go to the end lighting up the LEDs
                            and then go back to the start turning them off.
                         If JohnsonType.REACH, will light the LEDs until the end and then restart the sequence.
    :return: an array of arrays of RGB color tuples for making a light show
    """

    johnson_gradient = []
    for i in range(n_leds):
        step = []
        counter = 0
        for gc in color_values:
            step.append(gc) if counter < i else step.append((0, 0, 0))
            counter += 1
        johnson_gradient.append(step)

    if johnson_type == JohnsonType.FULL_RING:
        for i in range(n_leds):
            step = []
            counter = 0
            for gc in color_values:
                step.append(gc) if counter >= i else step.append((0, 0, 0))
                counter += 1
            johnson_gradient.append(step)

    elif johnson_type == JohnsonType.BEAT:
        # MODO ACIDENTAL BEATS BY DRE
        johnson_gradient.extend(johnson_gradient[-2::-1])

    return johnson_gradient
