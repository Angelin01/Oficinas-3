

from LightFunctions.gradient import gen_hue_gradient, handler_update_all
from controller import TimedLightShow


gradient = gen_hue_gradient(0, 360, 1, 0, 100)

light_show = TimedLightShow(handler_update_all, gradient, 4, 0.1, 15)

light_show.start()

light_show.join()
