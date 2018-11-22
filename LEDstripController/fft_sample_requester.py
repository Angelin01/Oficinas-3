import threading

from Tesseract.Audio.Audio import getLoopbackAudioData
from Tesseract.Light.LightFunctions.handlers import sound_analyzer


class FftSampleRequester:

    lock = threading.Lock()

    _fft_sample = None
    _sample_updated = False

    @staticmethod
    def request_new_sample():

        data = getLoopbackAudioData()

        with FftSampleRequester.lock:
            FftSampleRequester._fft_sample = sound_analyzer.getAmplitudes(data)
            _sample_updated = True

    @staticmethod
    def get_sample():

        with FftSampleRequester.lock:
            sample = FftSampleRequester._fft_sample

        return sample

    @staticmethod
    def set_is_sample_updated(new_value):

        with FftSampleRequester.lock:
            FftSampleRequester._sample_updated = new_value

    @staticmethod
    def is_sample_updated():

        with FftSampleRequester.lock:
            updated = FftSampleRequester._sample_updated

        return updated
