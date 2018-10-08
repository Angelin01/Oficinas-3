from .gradient import gen_hue_gradient


def gen_johnson_gradient(start_hue: int, end_hue: int, intensity: int, n_leds, gradient_backwards: bool = False,
                         full_ring: bool = True):
    """
    Generates a hue gradient but using a johnson counter, so that instead of changing colors the LEDs turn on/off
    sequentially
    :param start_hue: A value between 0 and 360 representing the color wheel.
    :param end_hue: A value between 0 and 360 representing the color wheel.
    :param n_leds: The number of LEDs in the sequence.
    :param intensity: The intensity value in which the gradient occurs.
    :param gradient_backwards: A bool to say if the gradient goes forward (from start to end if False) or the contrary
    :param full_ring: If True, the sequence will first turn on LEDs on then repeat but then turning them off. If False,
                      it will stop when all LEDs are on
    :return:
    """

    start_hue %= 360
    end_hue %= 360
    if gradient_backwards:
        start_hue, end_hue = end_hue, start_hue

    gradient_colors = gen_hue_gradient(start_hue, end_hue, (end_hue-start_hue)/(n_leds-1), intensity)

    johnson_gradient = []
    for i in range(n_leds):
        step = []
        counter = 0
        for gc in gradient_colors:
            step.append(gc) if counter < i else step.append((0, 0, 0))
            counter += 1
        johnson_gradient.append(step)

    if full_ring:
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
