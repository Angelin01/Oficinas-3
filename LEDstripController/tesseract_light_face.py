import threading
import time

from LEDstripController.LightFunctions.convert_strip import *
from LEDstripController.LightFunctions.handler_creators import *
from LEDstripController.LightFunctions.handlers import *


class TesseractLightFace:
    """
    Holds the light configuration for a face of the Tesseract.
    """

    handlers = {
        'standard': standard_handler,
        'stream': stream_handler,
        'wave': wave_handler,
        'breathe': breathe_handler,
        'fft_color': fft_color_handler,
        'fft_bars': fft_bars_handler
    }

    handler_constructors = {
        'standard': create_standard_handler_args,
        'stream': create_stream_handler_args,
        'wave': create_wave_handler_args,
        'breathe': create_breathe_handler_args,
        'fft_color': create_fft_color_handler_args,
        'fft_bars': create_fft_bars_handler_args
    }

    modifiers = {
        0: None,
        1: rising_ring,
        2: descending_ring,
        3: ltor_ring,
        4: rtol_ring,
    }

    def __init__(self):
        """
        Constructor.
        Initializes used attributes to None.
        """
        self.handler_function = None
        self.handler_args = None

        # The final modification to the generated color scheme - rising ring and the sorts.
        self.sequence_modifier = None

        # The time it takes for the values in this face to update.
        self.update_interval = None
        self.next_update = 0

        self.generated_sequence = None

        self.lock = threading.Lock()

        self.light_updater = threading.Thread(target=self.update)

    def set_new_config(self, handler_name: str, update_interval: float, config_args: dict, modifier_id: int):
        """
        :param handler_name: The key the 'handlers' dict that represents the desired handler.
        :param update_interval: The time interval this configuration updates.
        :param config_args: The properties of the new configuration.
        :param modifier_id: The modifier id to be applied to each generated color sequence.
        :return: Nothing.
        """

        with self.lock:
            self.handler_function = self.handlers[handler_name]
            self.handler_args = self.handler_constructors[handler_name](**config_args)

            self.sequence_modifier = self.modifiers[modifier_id]

            self.update_interval = update_interval

    def get_new_sequence(self):
        """
        Gets an updated list of colors to be sent to the strips.
        :return: The updated list.
        """

        FftSampleRequester.set_is_sample_updated(False)

        with self.lock:
            generated_sequence = self.generated_sequence

        return generated_sequence

    def update(self):
        """
        Checks if enough time has passed to update the generated color values list.
        :return:
        """

        while True:

            with self.lock:
                update_interval = self.update_interval

            time.sleep(update_interval)

            with self.lock:
                step = self.handler_function(self.handler_args)

                if self.sequence_modifier is not None:
                    step = self.sequence_modifier(step)

                self.generated_sequence = step
