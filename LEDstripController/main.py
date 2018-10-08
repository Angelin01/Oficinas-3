

from LightFunctions.gradient import gen_hue_gradient, hue_gradient_update_all
from controller import TimedLightShow

def main():
	gradient = gen_hue_gradient(0, 360, 1, 100, False)

	light_show = TimedLightShow(hue_gradient_update_all, gradient, 4, 0.1, 15)
	light_show.start()
	light_show.join()


if __name__ == "__main__":
	main()
