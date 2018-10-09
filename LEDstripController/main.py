from LightFunctions.gradient import gen_hue_gradient, hue_gradient_update_all
from LightFunctions.johnson_gradient import gen_johnson_gradient, johnson_gradient_update_all
from controller import TimedLightShow


def main():

    gradient = gen_johnson_gradient(0, 360, 100, 10)

    light_show = TimedLightShow(johnson_gradient_update_all, gradient, 10, 0.1, -1)

    light_show.start()

    input('Press any key to stop')

    light_show.stop()
    light_show.join()


if __name__ == "__main__":
    main()
