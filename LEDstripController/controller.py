import spidev
import ws2812

import threading

import time

from LightFunctions.convert_strip import rising_ring


class TimedLightShow(threading.Thread):

    def __init__(self, ref_function, ref_function_args, interval, duration):
        """
        :param ref_function: A reference to the function to be called.
        :param ref_function_args: The arguments to be passed to the function as a dictionary.
        :param interval: The interval in seconds between function calls.
        :param duration: For how long the function will remain active. If 0 or negative, is infinite.
        """
        super().__init__()

        self.ref_function = ref_function
        self.ref_function_args = ref_function_args
        self.interval = interval
        self.duration = duration

        self.stop_event = threading.Event()

        self.is_inf = True if duration <= 0 else False

        self.end_of_show = time.time() + duration

        self.spi = spidev.SpiDev()
        self.spi.open(0, 0)

    def run(self):

        while True:
            result = self.ref_function(self.ref_function_args)
            result = rising_ring(result)

            time.sleep(self.interval)

            ws2812.write2812(self.spi, result)

            if self.stop_event.is_set():
                break

            if not self.is_inf and time.time() > self.end_of_show:
                break

    def stop(self):
        self.stop_event.set()
