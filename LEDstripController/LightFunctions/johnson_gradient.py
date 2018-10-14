from .gradient import gen_hue_gradient
from enum import Enum


class JohnsonType(Enum):
    FULL_RING = 0
    BEAT = 1
    REACH = 2


def gen_johnson_gradient(start_hue: int, end_hue: int, intensity: int, n_leds: int, gradient_backwards: bool = False,
                         johnson_type: JohnsonType = JohnsonType.FULL_RING):
    """
    Generates a hue gradient but using a johnson counter, so that instead of changing colors the LEDs turn on/off
    sequentially
    :param start_hue: A value between 0 and 360 representing the color wheel.
    :param end_hue: A value between 0 and 360 representing the color wheel.
    :param n_leds: The number of LEDs in the sequence.
    :param intensity: The intensity value in which the gradient occurs.
    :param gradient_backwards: A bool to say if the gradient goes forward (from start to end if False) or the contrary
    :param johnson_type: If JohnsonType.FULL_RING, the sequence will first turn on LEDs on then repeat,
                            but then turning them off.
                         If JohnsonType.BEAT, the sequence will first go to the end lighting up the LEDs
                            and then go back to the start turning them off.
                         If JohnsonType.REACH, will light the LEDs until the end and then restart the sequence.
    :return: an array of arrays of RGB color tuples for making a light show
    """

    start_hue %= 361
    end_hue %= 361

    if gradient_backwards:
        start_hue, end_hue = end_hue, start_hue

    gradient_colors = [color[0] for color in gen_hue_gradient(start_hue, end_hue, (end_hue - start_hue) / (n_leds - 1), intensity, n_leds)]

    johnson_gradient = []
    for i in range(n_leds):
        step = []
        counter = 0
        for gc in gradient_colors:
            step.append(gc) if counter < i else step.append((0, 0, 0))
            counter += 1
        johnson_gradient.append(step)

    if johnson_type == JohnsonType.FULL_RING:
        for i in range(n_leds):
            step = []
            counter = 0
            for gc in gradient_colors:
                step.append(gc) if counter >= i else step.append((0, 0, 0))
                counter += 1
            johnson_gradient.append(step)

    elif johnson_type == JohnsonType.BEAT:
        # MODO ACIDENTAL BEATS BY DRE
        johnson_gradient.extend(johnson_gradient[-2::-1])

    return johnson_gradient


def johnson_gradient_update_all(values_sequence, step, __):
    """
    Updater used by the controller to get the next step in a johnson_gradient value sequence.
    Ignores the regular n_leds parameter, since it the gen_johnson_gradient already uses it
    :param values_sequence: a johnson_gradient value sequence.
    :param step: Which of the values to return in the sequence.
    :param __: Normally the number of LEDs. Not used, for compatibility purposes only.
    :return:
    """
    return values_sequence[step]
