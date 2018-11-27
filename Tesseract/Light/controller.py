import spidev
from Light import ws2812

import threading
import time

from Light.LightFunctions.random import gen_random
from Light.light_config_parser import parse_light_config
from Light.tesseract_light_face import TesseractLightFace


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
            0: TesseractLightFace(),
            1: TesseractLightFace(),
            2: TesseractLightFace(),
            3: TesseractLightFace()
        }

        self.stop_event = threading.Event()

        self.controller_interval = 0.010  # 10 ms
        self.controller_interval_fraction = self.controller_interval / 5  # To be used by the timer.

        self.next_strip_update = 0

        self.spi = spidev.SpiDev()
        self.spi.open(0, 0)

        self.current_time = 0

        self.is_shuffling = False

        self.acc_queue_watcher = threading.Thread(target=self.acc_queue_msg_watcher)
        self.bluetooth_queue_watcher = threading.Thread(target=self.bluetooth_queue_msg_watcher)

        self.acc_lock = threading.Lock()
        self.blue_lock = threading.Lock()

    def bluetooth_queue_msg_watcher(self):

        while True:
            try:
                msg = self.bluetooth_queue.get()

                print(msg)

                parsed_configs = parse_light_config(msg)

                with self.blue_lock:
                    for config in parsed_configs:
                        self.update_face_config(**config)
            except Exception as exception:
                print('invalid message sent from bluetooth process to light process: ', exception)

    def acc_queue_msg_watcher(self):

        while True:
            try:
                msg = self.acc_queue.get()

                if 'config' not in msg:
                    continue

                if msg['config'] == 'shuffle':
                    with self.acc_lock:
                        self.is_shuffling = True

                    self.handle_shuffle_effect()

                    with self.acc_lock:
                        self.is_shuffling = False
            except Exception as exception:
                print('invalid message sent from accelerometer process to light process: ', exception)

    def handle_shuffle_effect(self):
        """
        When a message saying that the shuffle was enabled,
        interrupts the current led animations, and performs a special random animation.
        :return:
        """

        random_sequence_len = 20
        random_sequence = gen_random(80, random_sequence_len)

        sleep_time = 0.030
        step = 0

        while step < 40:
            ws2812.write2812(self.spi, random_sequence[step % random_sequence_len])
            step += 1
            time.sleep(sleep_time)
            sleep_time -= 0.00025

        for i in range(2):
            time.sleep(0.15)
            ws2812.write2812(self.spi, random_sequence[step % random_sequence_len])
            step += 1

    def update_face_config(self, **kwargs):
        """
        :param kwargs: Must contain fields named:
            'face_id', 'handler_id', 'update_interval', 'config_args', 'modifier_id'.

            The 'face_id' attribute is used by this controller and must be and int.
            The others are used by the TesseractLightFace object. See it for more details.

        :return: Nothing.
        """

        self.light_faces[kwargs['face_id']].set_new_config(
            kwargs['pattern'],
            kwargs['update_interval'],
            kwargs['config_args'],
            kwargs['modifier_id']
        )

    def run(self):

        self.acc_queue_watcher.start()
        self.bluetooth_queue_watcher.start()

        while True:
            try:
                with self.acc_lock:
                    is_shuffling = self.is_shuffling

                if is_shuffling:
                    time.sleep(self.controller_interval)
                    continue

                update_start = time.time()
                TesseractLightFace.current_timestamp = update_start

                with self.blue_lock:
                    for i in range(4):
                        self.light_faces[i].update()

                update_time = time.time() - update_start

                iteration_sleep_time = self.controller_interval - update_time

                result = (self.light_faces[0].get_new_sequence() + self.light_faces[1].get_new_sequence() +
                          self.light_faces[2].get_new_sequence() + self.light_faces[3].get_new_sequence())

                # Sending results to the strip.
                ws2812.write2812(self.spi, result)

                if iteration_sleep_time > 0:
                    # Sleeps the thread for a moment.
                    time.sleep(iteration_sleep_time)

                if self.stop_event.is_set():
                    break
            except Exception as exception:
                print('LightService exception: ', exception)

    def stop(self):
        self.stop_event.set()
