import random
from sys import maxsize


def gen_random(n_steps: int = 20, seed: int = None):
    """
    Generates random leds blinking randomly for random reasons.
    :param n_leds: The number of LEDs to blink.
    :param n_steps: How many different colors per LED to generate. If this number is too low, the blinking lights will
                    appear to be a pattern (which it is) instead of random.
    :param seed: The seed for the generator. If None, will use the system time.
    :return: an array of random seeds for generating the tuples
    """
    return [random.randrange(maxsize) for _ in range(n_steps)]


def random_update_all(values_sequence, step, n_led):
    random.seed(values_sequence[step])
    return [(random.randrange(256), random.randrange(256), random.randrange(256)) for _ in range(n_led)]
