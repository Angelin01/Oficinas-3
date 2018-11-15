from LightFunctions.color_gen import gen_rainbow_gradient
from LightFunctions.handler_creators import create_wave_handler_args
from LightFunctions.handlers import standard_handler, wave_handler
from LightFunctions.modifiers import wave_modifier, gen_sine_wave
from controller import TimedLightShow


def main():

    n_leds = 20

    gradient = gen_rainbow_gradient(0, 360, 1, 100)
    wave = gen_sine_wave(0.5, 3, n_leds)
    wave_handler_args = create_wave_handler_args(gradient, wave, 3, n_leds)

    light_show = TimedLightShow(wave_handler, wave_handler_args, 0.05, -1)

    light_show.start()

    input('Press any key to stop')

    light_show.stop()
    light_show.join()


if __name__ == "__main__":
    main()
