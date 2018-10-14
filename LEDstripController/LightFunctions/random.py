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
