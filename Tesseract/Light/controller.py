from Audio import Audio
from FrequencyAnalyzer.SoundAnalyzer import SoundAnalyzer
import spidev
import Light.ws2812 as ws2812

import multiprocessing

import time

from Light.LightFunctions.convert_strip import rising_ring

from Audio.Audio import getLoopbackAudioData


class TimedLightShow(multiprocessing.Process):

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

        self.stop_event = multiprocessing.Event()

        self.is_inf = True if duration <= 0 else False

        self.end_of_show = time.time() + duration

        self.spi = spidev.SpiDev()
        self.spi.open(0, 0)

        self.sound_analyzer = SoundAnalyzer(Audio.chunk_size)

    def run(self):

        while True:
            data = getLoopbackAudioData()
            amplitudes = self.sound_analyzer.getAmplitudes(data)
            print(amplitudes)

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
