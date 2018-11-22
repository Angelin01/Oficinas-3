from LEDstripController.LightFunctions.color_gen import gen_rainbow_gradient, gen_color_gradient
from LEDstripController.LightFunctions.modifiers import gen_sine_wave
from LEDstripController.color import ColorMode


face_name_to_id = {
    'front': 0,
    'right': 1,
    'left': 2,
    'back': 3
}

modifier_name_to_id = {
    'none': 0,
    'rising': 1,
    'descending': 2,
    'left_to_right': 3,
    'right_to_left': 4
}


def parse_light_config(light_configs: list):
    """
    :param light_configs: A list of dicts received from the mobile user.
    :return: The parameters to be used by the handler creators.
    """
    parsed_light_configs = []

    for light_config in light_configs:

        colors = light_config['colors']

        formatted_colors = []
        for c in colors:
            formatted_colors.append(parse_hex_value_color(c))

        light_config = {
            'pattern': light_config['pattern'],
            'intensity': light_config['intensity'],
            'speed': light_config['speed'],
            'face_id': face_name_to_id[light_config['face']],
            'modifier_id': modifier_name_to_id[light_config['modifier']],
            'colors': formatted_colors,
            'update_interval': 0.020
        }

        if light_config['pattern'] == 'wave':
            complete_wave_config(light_config)

        elif light_config['pattern'] == 'breathe':
            complete_breathe_config(light_config)

        elif light_config['pattern'] == 'stream':
            complete_stream_config(light_config)

        elif light_config['pattern'] == 'fft_color':
            complete_fft_color(light_config)

        elif light_config['pattern'] == 'fft_bars':
            complete_fft_bars(light_config)

        parsed_light_configs.append(light_config)

    return parsed_light_configs


def complete_wave_config(light_config: dict):

    sine_wave = gen_sine_wave(light_config['speed'], 2, 20)

    cs = gen_color_gradient(light_config['speed'], light_config['intensity'], *light_config['colors'])

    config_args = {
        'color_sequence': cs,
        'wave': sine_wave,
        'color_delay': 5,
        'n_leds': 20,

    }

    light_config['config_args'] = config_args


def complete_breathe_config(light_config: dict):

    sine_wave = gen_sine_wave(light_config['speed'], 2, 20)

    cs = gen_rainbow_gradient(0, 360, light_config['speed'], light_config['intensity'])

    config_args = {
        'color_sequence': cs,
        'wave': sine_wave,
        'color_delay': 5,
        'n_leds': 20,

    }

    light_config['config_args'] = config_args


def complete_stream_config(light_config: dict):

    cs = gen_rainbow_gradient(0, 360, light_config['speed'], light_config['intensity'])

    config_args = {
        'color_sequence': cs,
        'n_leds': 20,
    }

    light_config['config_args'] = config_args


def complete_fft_color(light_config: dict):

    config_args = {
        'min_color': light_config['colors'][0],
        'max_color': light_config['colors'][1],
        'resolution': 1,
        'intensity': light_config['intensity'],
        'color_mode': ColorMode.RGB
    }

    light_config['config_args'] = config_args


def complete_fft_bars(light_config: dict):

    config_args = {
        'low_color': light_config['colors'][0],
        'mid_color': light_config['colors'][1],
        'high_color': light_config['colors'][2],
        'intensity': light_config['intensity'],
        'color_mode': ColorMode.RGB
    }

    light_config['config_args'] = config_args


def parse_hex_value_color(hex_value_color: str):

    if hex_value_color[0] == '#':
        hex_value_color = hex_value_color[1:]

    red = int(hex_value_color[0:2], base=16)
    green = int(hex_value_color[2:4], base=16)
    blue = int(hex_value_color[4:6], base=16)

    return red, green, blue
