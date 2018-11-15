from color import Color, ColorMode


def gen_rainbow_gradient(start_hue: int, end_hue: int, speed: float, intensity: int, gradient_backwards: bool = False):
    """
    Generates an iterator to make the transition from start_hue to end_hue with certain speed.
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

    rainbow_colors = [Color(hue / 360, 1, 0.5, ColorMode.HSL) for hue in hue_steps]

    Color.batch_hsl_to_rgb(rainbow_colors)

    rainbow_colors = [gc.get_ws2812_rgb(intensity) for gc in rainbow_colors]

    return rainbow_colors


def gen_color_gradient(speed: float, intensity: int, *colors, color_mode=ColorMode.RGB):
    """
    Generates all intermediate colors between two colors. Does this for a sequence of n colors.

    :param speed: The speed of the gradient change.
    :param intensity: The intensity value in which the gradient occurs. Between 0 and 255.
    :param colors: An array of RGB or HSL values. All colors in the list must be from the same color space.
                   Each channel is a value between 0 and 1.
    :param color_mode: The color space the colors belong to.
    :return: An array of GRB tuples.
    """

    n_colors = len(colors)
    color_change_steps = int(10 * speed)

    gradient_colors = []

    # Generating colors between neighboring ones.
    for i in range(len(colors)):
        color_start = colors[i]
        color_end = colors[(i + 1) % n_colors]

        for s in range(color_change_steps):
            lerp_color = color_lerp(color_start, color_end, s / 10)
            gradient_colors.append(Color(*lerp_color, color_mode))

    # Closing the loop.
    gradient_colors.append(Color(*colors[0], color_mode))

    if color_mode == ColorMode.HSL:
        Color.batch_hsl_to_rgb(gradient_colors)

    # Setting intensity.
    gradient_colors = [gc.get_ws2812_rgb(intensity) for gc in gradient_colors]

    return gradient_colors


def color_lerp(start_color: tuple, end_color: tuple, t: float):
    """
    :param start_color: The color from the gradient starts.
    :param end_color: The color at the gradient ends.
    :param t: The value from 0 to 1 that generates a color between the two colors.
    :return: A new color between the values.
    """
    return [(1 - t) * start_color[i] + t * end_color[i] for i in range(3)]


