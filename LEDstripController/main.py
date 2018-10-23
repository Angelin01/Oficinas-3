from LightFunctions.color_gen import gen_rainbow_gradient
from LightFunctions.handlers import standard_handler
from LightFunctions.modifiers import wave_modifier
from controller import TimedLightShow


def main():

    gradient = gen_rainbow_gradient(0, 360, 1, 100)

    wave = wave_modifier(gradient, 5, 0.5, 2, 20)

    light_show = TimedLightShow(standard_handler, wave, 0.1, -1)

    light_show.start()

    input('Press any key to stop')

    light_show.stop()
    light_show.join()


if __name__ == "__main__":
    main()
