import colorsys


# Methods for color processing.
# Offers methods to transform between RGB and HSL color spaces.

# All channels in both RGB and HSL are represented between 0 and 1.

class ColorMode:
    
    RGB = 0
    HSL = 1


class Color:

    def __init__(self, c1, c2, c3, color_mode=ColorMode.RGB):
        """
        :param c1: Color channel 1 - R when color_mode == RGB; H when color_mode == HSL
        :param c2: Color channel 2 - G when color_mode == RGB; S when color_mode == HSL
        :param c3: Color channel 3 - B when color_mode == RGB; L when color_mode == HSL
        :param color_mode: An int to represent the color space. RGB (0) and HSL (1)
        """

        self.is_rgb_updated = False
        self.is_hsl_updated = False

        self.color_mode = color_mode

        if color_mode == ColorMode.RGB:
            self.rgb = (c1, c2, c3)

            self.h = None
            self.s = None
            self.l = None

        elif color_mode == ColorMode.HSL:
            self.hsl = (c1, c2, c3)

            self.r = None
            self.g = None
            self.b = None

        else:
            raise AttributeError('Color mode not supported.')

    @property
    def rgb(self):
        """
        RGB values getter.
        :return: A tuple with the RGB values.
        """

        return self.r, self.g, self.b

    @rgb.setter
    def rgb(self, value):
        """
        RGB values setter. Values must be between 0 and 1, inclusive.
        :param value: A tuple containing RGB values.
        """
        self.r, self.g, self.b = value

    @property
    def hsl(self):
        """
        HSL values getter.
        :return: A tuple with the HSL values.
        """
        return self.h, self.s, self.l

    @hsl.setter
    def hsl(self, value):
        """
        HSL values setter. Values must be between 0 and 1, inclusive.
        :param value: A tuple containing HSL values.
        """
        self.h, self.s, self.l = value

    def get_ws2812_rgb(self, intensity):
        """
        :return: A tuple of GRB values, the order used by the ws2812 strip, in a range between 0 and intensity.
        """
        r = int(self.r * intensity)
        g = int(self.g * intensity)
        b = int(self.b * intensity)

        return g, r, b

    def hsl_to_rgb(self):
        """
        Transforms color from the HSL color space to the RGB color space.
        """
        if self.color_mode == ColorMode.RGB:
            return

        self.h, self.l, self.s = colorsys.rgb_to_hls(self.r, self.g, self.b)

        self.color_mode = ColorMode.RGB

    def rgb_to_hsl(self):
        """
        Transforms color from the RGB color space to the HSL color space.
        """
        if self.color_mode == ColorMode.HSL:
            return

        self.r, self.g, self.b = colorsys.hls_to_rgb(self.h, self.l, self.s)

        self.color_mode = ColorMode.HSL

    # Passes color list by reference since it is a list.
    @staticmethod
    def batch_hsl_to_rgb(color_list):
        """
        Transforms HSL to RGB in place.

        :param color_list: A list of Color objects.
        Transforms color from the HSL color space to the RGB color space.
        """

        rgb_values = [colorsys.hls_to_rgb(color.h, color.l, color.s) for color in color_list]

        for i in range(len(color_list)):
            color_list[i].rgb = rgb_values[i]
            color_list[i].color_mode = ColorMode.RGB

    @staticmethod
    def batch_rgb_to_hsl(color_list):
        """
        Transforms RGB to HSL in place.

        :param color_list: A list of Color objects.
        Transforms color from the RGB color space to the HSL color space.
        """

        hls_values = [colorsys.rgb_to_hls(color.r, color.g, color.b) for color in color_list]

        # Setting HSL values.
        # Attention to result of the color space transform function that changes the order of params.
        for i in range(len(color_list)):
            color_list[i].h = hls_values[i][0]
            color_list[i].s = hls_values[i][2]
            color_list[i].l = hls_values[i][1]
            color_list[i].color_mode = ColorMode.HSL


class SafeColor:
    """
    Offers the same functionality as the class Color.
    SafeColor performs value boundary checks and auto color space conversion during get operations.
    """

    def __init__(self, c1, c2, c3, color_mode=ColorMode.RGB):
        """
        :param c1: Color channel 1 - R when color_mode == RGB; H when color_mode == HSL
        :param c2: Color channel 2 - G when color_mode == RGB; S when color_mode == HSL
        :param c3: Color channel 3 - B when color_mode == RGB; L when color_mode == HSL
        :param color_mode: An int to represent the color space. RGB (0) and HSL (1)
        """

        self.is_rgb_updated = False
        self.is_hsl_updated = False

        self.color_mode = color_mode

        if color_mode == ColorMode.RGB:
            self.rgb = (c1, c2, c3)

            self.h = None
            self.s = None
            self.l = None

        elif color_mode == ColorMode.HSL:
            self.hsl = (c1, c2, c3)

            self.r = None
            self.g = None
            self.b = None

        else:
            raise AttributeError('Color mode not supported.')

    @property
    def rgb(self):
        """
        RGB values getter.
        Transforms to the RGB color space if the color was updated in another color space.
        :return: A tuple with the RGB values.
        """
        if self.color_mode != ColorMode.RGB and self.is_hsl_updated:
            self.hsl_to_rgb()

        return self.r, self.g, self.b

    @rgb.setter
    def rgb(self, value):
        """
        RGB values setter.
        Clamps the values between 0 and 1 - the range used by the color space transforming functions.
        :param value: A tuple containing RGB values.
        """
        self.r = min(max(0, value[0]), 1)
        self.g = min(max(0, value[1]), 1)
        self.b = min(max(0, value[2]), 1)

        self.is_rgb_updated = True

    @property
    def hsl(self):
        """
        HSL values getter.
        :return: A tuple with the HSL values.
        """
        if self.color_mode != ColorMode.HSL and self.is_rgb_updated:
            self.rgb_to_hsl()

        return self.h, self.s, self.l

    @hsl.setter
    def hsl(self, value):
        """
        HSL values setter.
        Clamps the values between 0 and 1 - the range used by the color space transforming functions.
        :param value: A tuple containing HSL values.
        """
        self.h = min(max(0, value[0]), 1)
        self.s = min(max(0, value[1]), 1)
        self.l = min(max(0, value[2]), 1)

        self.is_hsl_updated = True

    def get_int_rgb(self):
        """
        :return: A tuple of RGB values in a range between 0 and 255.
        """

        if self.color_mode != ColorMode.RGB:
            self.hsl_to_rgb()

        r = int(self.r * 255)
        g = int(self.g * 255)
        b = int(self.b * 255)

        return r, g, b

    def hsl_to_rgb(self):
        """
        Transforms color from the HSL color space to the RGB color space.
        """
        if self.color_mode == ColorMode.RGB:
            return

        self.h, self.l, self.s = colorsys.rgb_to_hls(self.r, self.g, self.b)

        self.color_mode = ColorMode.RGB

    def rgb_to_hsl(self):
        """
        Transforms color from the RGB color space to the HSL color space.
        """
        if self.color_mode == ColorMode.HSL:
            return

        self.r, self.g, self.b = colorsys.hls_to_rgb(self.h, self.l, self.s)

        self.color_mode = ColorMode.HSL

    # Passes color list by reference since it is a list.
    @staticmethod
    def batch_hsl_to_rgb(color_list):
        """
        :param color_list: A list of Color objects.
        Transforms color from the HSL color space to the RGB color space.
        :return:
        """

        rgb_values = [colorsys.hls_to_rgb(color.h, color.l, color.s) for color in color_list]

        for i in range(len(color_list)):
            color_list[i].r = rgb_values[i][0]
            color_list[i].g = rgb_values[i][1]
            color_list[i].b = rgb_values[i][2]
            color_list[i].color_mode = ColorMode.RGB

    # Passes color list by reference since it is a list.
    @staticmethod
    def batch_rgb_to_hsl(color_list):
        """
        :param color_list: A list of Color objects.
        Transforms color from the RGB color space to the HSL color space.
        :return:
        """

        hls_values = [colorsys.rgb_to_hls(color.r, color.g, color.b) for color in color_list]

        for i in range(len(color_list)):
            color_list[i].h = hls_values[i][0]
            color_list[i].s = hls_values[i][2]
            color_list[i].l = hls_values[i][1]
            color_list[i].color_mode = ColorMode.HSL


c = Color(2, 0.5, -2)
