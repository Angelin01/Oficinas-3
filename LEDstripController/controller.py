import spidev
import ws2812

import threading

import time


class TimedLightShow(threading.Thread):

    def __init__(self, ref_function, sequence, n_led, interval, duration):
        """
        :param ref_function: A reference to the function to be called.
        :param sequence: The arguments to be passed to the function as a tuple.
        :param n_led: The number of LEDs the sequence should be applied to.
        :param interval: The interval in seconds between function calls.
        :param duration: For how long the function will remain active. If 0 or negative, is infinite.
        """
        super().__init__()

        self.ref_function = ref_function
        self.sequence_len = len(sequence)
        self.sequence = sequence
        self.n_led = n_led
        self.interval = interval
        self.duration = duration

        self.stop_event = threading.Event()

        self.is_inf = True if duration <= 0 else False

        self.end_of_show = time.time() + duration

        self.spi = spidev.SpiDev()
        self.spi.open(0, 0)

    def run(self):
        step = 0

        while True:
            result = self.ref_function(self.sequence, step, self.n_led)
            ws2812.write2812(self.spi, result)

            time.sleep(self.interval)
            step = (step + 1) % self.sequence_len

            if self.stop_event.is_set():
                break

            if not self.is_inf and time.time() > self.end_of_show:
                break

    def stop(self):
        self.stop_event.set()
