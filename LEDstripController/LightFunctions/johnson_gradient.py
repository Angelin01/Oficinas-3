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
    pass
