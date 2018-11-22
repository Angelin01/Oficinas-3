import spidev
import ws2812

import threading
import time

from LEDstripController.tesseract_light_face import TesseractLightFace


class TimedLightShow(threading.Thread):

    def __init__(self, bluetooth_queue, acc_queue):
        """
        :param bluetooth_queue: The queue that receives stuff from the Bluetooth service
        :param acc_queue: The queue that receives stuff from the Accelerometer service
        """
        super().__init__()

        self.bluetooth_queue = bluetooth_queue
        self.acc_queue = acc_queue

        self.light_faces = {
            1: TesseractLightFace(),
            2: TesseractLightFace(),
            3: TesseractLightFace(),
            4: TesseractLightFace()
        }

        self.stop_event = threading.Event()

        self.controller_interval = 0.010  # 10 ms
        self.controller_interval_fraction = self.controller_interval / 5  # To be used by the timer.

        self.next_strip_update = 0

        self.spi = spidev.SpiDev()
        self.spi.open(0, 0)

        self.current_time = 0

    def update_face_config(self, **kwargs):
        """
        :param kwargs: Must contain fields named:
            'face_id', 'handler_id', 'update_interval', 'config_args', 'modifier_id'.

            The 'face_id' attribute is used by this controller and must be and int.
            The others are used by the TesseractLightFace object. See it for more details.

        :return: Nothing.
        """

        self.light_faces[kwargs['face_id']].set_new_config(
            kwargs['handler_id'], kwargs['update_interval'], kwargs['config_args'], kwargs['modifier_id'])

    def run(self):

        while True:

            result = [self.light_faces[i].get_new_sequence() for i in range(1, 5)]

            # Getting current timestamp.
            self.current_time = time.time()
            TesseractLightFace.current_time = self.current_time

            # Waiting until next update comes.
            while self.current_time < self.next_strip_update:
                # Sleeps the thread for a moment.
                time.sleep(self.controller_interval_fraction)

                # Trying to generate new color sequences.
                for i in range(1, 5):
                    self.light_faces[i].update()

                # Updating current timestamp.
                self.current_time = time.time()
                TesseractLightFace.current_time = self.current_time

            # Sending results to the strip.
            ws2812.write2812(self.spi, result)

            # Updating the next timestamp for an update.
            self.next_strip_update += self.controller_interval

            if self.stop_event.is_set():
                break

    def stop(self):
        self.stop_event.set()
