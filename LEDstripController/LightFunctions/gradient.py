from color import Color, ColorMode



def gen_hue_gradient(start_hue: int, end_hue: int, speed: float, intensity: int, n_leds: int, gradient_backwards: bool = False):
    """
    Generates an iterator to make the transition from start_hue to end_hue with certain speed for n_leds.
    The color wheel is a cycle, so if end_hue is less than start_hue, it will loop.

    :param start_hue: A value between 0 and 360 representing the color wheel.
    :param end_hue: A value between 0 and 360 representing the color wheel.
    :param speed: The speed of the gradient change.
    :param intensity: The intensity value in which the gradient occurs. Between 0 and 255.
    :param gradient_backwards: A bool to say if the gradient goes forward (from start to end if False) or the contrary.
    :return: An array of GRB tuples.
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

    return [[color]*n_leds for color in gradient_colors]
