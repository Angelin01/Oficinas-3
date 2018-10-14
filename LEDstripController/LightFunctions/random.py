from numpy.random import randint


def gen_random(n_leds: int, n_steps: int = 20):
    """
    Generates random leds blinking randomly for random reasons.
    :param n_leds: The number of LEDs to blink.
    :param n_steps: How many different colors per LED to generate. If this number is too low, the blinking lights will
                    appear to be a pattern (which it is) instead of random.
    :return: a numpy array of GRB values for each led for each step
    """
    return randint(256, size=(n_steps, n_leds, 3))


def random_update_all(values_sequence, step, __):
    """
    Simple updater for a random
    Ignores the regular n_leds parameter, since it the gen_random already uses it
    :param values_sequence: a johnson_gradient value sequence.
    :param step: Which of the values to return in the sequence.
    :param __: Normally the number of LEDs. Not used, for compatibility purposes only.
    :return: The sequence to use for the LEDs.
    """
    return values_sequence[step]
