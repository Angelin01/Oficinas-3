from LightFunctions.color_gen import color_lerp
from controllerUtils import rotate_list_left

from Audio.Audio import getLoopbackAudioData
from FrequencyAnalyzer.SoundAnalyzer import SoundAnalyzer

from LEDstripController.color import ColorMode, Color
from LEDstripController.controllerUtils import sep_bar_levels
from LEDstripController.fft_sample_requester import FftSampleRequester
from LEDstripController.tesseract_light_face import TesseractLightFace


def standard_handler(args_dict: dict):
    """
    Handles the update of a standard pre computed function.
    :param args_dict: Created by the 'create_standard_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    step = args_dict['step']

    # Updating step for the next iteration.
    args_dict['step'] = (step + 1) % args_dict['sequence_len']

    return color_sequence[step]


def stream_handler(args_dict: dict):
    """
    The stream effect has the colors of the LEDs move like a stream, where the color of a LEDs is given to its
    neighbor in the next update step.
    :param args_dict: Created by the 'create_standard_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    color_seq_len = args_dict['color_seq_len']
    color_itr = args_dict['color_itr']
    n_leds = args_dict['n_leds']

    step_sequence = [color_sequence[c % color_seq_len] for c in range(color_itr, n_leds + color_itr)]

    # Updating step for the next iteration.
    args_dict['color_itr'] = (color_itr + 1) % color_seq_len

    return step_sequence


def wave_handler(args_dict: dict):
    """
    Handles the update of a wave function.
    :param args_dict: Created by the 'create_wave_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    wave = args_dict['wave']
    color_delay = args_dict['color_delay']
    n_leds = args_dict['n_leds']
    delay_itr = args_dict['delay_itr']
    color_itr = args_dict['color_itr']

    step_sequence = []

    color_seq_len = args_dict['color_seq_len']
    wave_len = args_dict['wave_len']

    for l in range(n_leds):

        step_sequence.append(
            [int(color_sequence[color_itr][channel] * wave[l % wave_len]) for channel in range(3)])

        delay_itr += 1

        if delay_itr % color_delay == 0:
            delay_itr = 0
            color_itr += 1
            if color_itr == color_seq_len:
                color_itr = 0

    # Updating wave for next iteration.
    rotate_list_left(wave)

    args_dict['delay_itr'] = delay_itr
    args_dict['color_itr'] = color_itr

    return step_sequence


def breathe_handler(args_dict: dict):
    """
    The breathe effect is comprised of all LEDs changing intensity in a sinusoidal manner, imitating an
    inflating balloon.
    :param args_dict: Created by the 'create_wave_handler_args' function.
    :return: a 'step' to be used by the controller.
    """

    color_sequence = args_dict['color_sequence']
    wave = args_dict['wave']
    color_delay = args_dict['color_delay']
    n_leds = args_dict['n_leds']
    delay_itr = args_dict['delay_itr']
    color_itr = args_dict['color_itr']

    color_seq_len = args_dict['color_seq_len']

    step_sequence = [int(color_sequence[color_itr][channel] * wave[0]) for channel in range(3)] * n_leds

    delay_itr += 1

    if delay_itr % color_delay == 0:
        delay_itr = 0
        color_itr += 1
        if color_itr == color_seq_len:
            color_itr = 0

    # Updating wave for next iteration.
    rotate_list_left(wave)

    args_dict['delay_itr'] = delay_itr
    args_dict['color_itr'] = color_itr

    return step_sequence


def fft_color_handler(args_dict: dict):

    min_intensity_color = args_dict['min_color']
    max_intensity_color = args_dict['max_color']
    resolution = args_dict['resolution']
    intensity = args_dict['intensity']
    max_sample = args_dict['max_fft_sample']
    color_mode = args_dict['color_mode']

    # Checking if must update the fft samples.
    # If all faces require a fft sample, uses the same one.
    if not FftSampleRequester.is_sample_updated():
        FftSampleRequester.request_new_sample()

    fft_result = FftSampleRequester.get_sample()

    updated_led_sequence = []

    for i in range(0, len(fft_result), resolution):

        norm_sample = 0
        for sample in range(i, i + resolution):
            norm_sample += fft_result[sample]

        norm_sample /= resolution

        if norm_sample > max_sample:
            max_sample = norm_sample

        norm_sample /= max_sample

        led_color = color_lerp(min_intensity_color, max_intensity_color, norm_sample)

        if color_mode == ColorMode.RGB:
            led_color = [c / 255 for c in led_color]

            for sample in range(resolution):
                updated_led_sequence.append(Color(*led_color, color_mode=color_mode))
        else:
            led_color = led_color[0] / 360, led_color[1] / 100, led_color[2] / 100

            for sample in range(resolution):
                updated_led_sequence.append(Color(*led_color, color_mode=color_mode))

    # Checking if must transform back to RGB.
    if color_mode == ColorMode.HSL:
        # Batch color space transformation.
        Color.batch_hsl_to_rgb(updated_led_sequence)

    # Getting int values.
    updated_led_sequence = [color.get_ws2812_rgb(intensity) for color in updated_led_sequence]

    args_dict['max_fft_sample'] = max_sample

    return updated_led_sequence


def fft_bars_handler(args_dict: dict):

    low_freq_color = args_dict['low_color']
    mid_freq_color = args_dict['mid_color']
    high_freq_color = args_dict['high_color']
    max_intensity = args_dict['intensity']
    max_sample = args_dict['max_sample']

    if not FftSampleRequester.is_sample_updated():
        FftSampleRequester.request_new_sample()

    fft_result = FftSampleRequester.get_sample()

    # Separating frequencies.
    lows = fft_result[0:6]
    mids = fft_result[6:14]
    highs = fft_result[14:20]

    # Getting greatest sample.
    max_low = max(lows)
    max_mid = max(mids)
    max_high = max(highs)

    # Checking if the greatest sample is greater than the one stored.
    current_max_sample = max(max_low, max_mid, max_high)

    if current_max_sample > max_sample:
        max_sample = current_max_sample
        args_dict['max_sample'] = max_sample

    # Setting a scale to be proportioned to 3 LEDs fully lit, according to intensity.
    scale = 3 * max_intensity / max_sample

    # Getting the levels according the scale.
    low_bar_level = max_low * scale
    mid_bar_level = max_mid * scale
    high_bar_level = max_high * scale

    # Getting the intensities for each of the 3 LEDs.
    lows_bar_intensities = sep_bar_levels(low_bar_level, max_intensity)
    mids_bar_intensities = sep_bar_levels(mid_bar_level, max_intensity)
    highs_bar_intensities = sep_bar_levels(high_bar_level, max_intensity)

    # Getting the colors according to the intensity for each LED.
    lows_bar = [low_freq_color.get_ws2812_rgb(i) for i in lows_bar_intensities]
    mids_bar = [mid_freq_color.get_ws2812_rgb(i) for i in mids_bar_intensities]
    highs_bar = [high_freq_color.get_ws2812_rgb(i) for i in highs_bar_intensities]

    # Duplicating to the side to form the full 5 LED side.
    lows_bar = lows_bar[:0:-1] + lows_bar
    mids_bar = mids_bar[:0:-1] + mids_bar
    highs_bar = highs_bar[:0:-1] + highs_bar

    # Returning the colors in the order the strip runs the Tesseract.
    return lows_bar + mids_bar + highs_bar + mids_bar
