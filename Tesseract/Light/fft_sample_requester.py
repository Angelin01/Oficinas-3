from Audio.Audio import getLoopbackAudioData
from FrequencyAnalyzer.SoundAnalyzer import SoundAnalyzer


class FftSampleRequester:

    _fft_sample = None
    _sample_updated = False

    @staticmethod
    def request_new_sample():

        data = getLoopbackAudioData()
        sound_analyzer = SoundAnalyzer(512) # 2048)
        FftSampleRequester._fft_sample = sound_analyzer.getAmplitudes(data)
        FftSampleRequester._sample_updated = True

    @staticmethod
    def get_sample():
        return FftSampleRequester._fft_sample

    @staticmethod
    def set_is_sample_updated(new_value):
        FftSampleRequester._sample_updated = new_value

    @staticmethod
    def is_sample_updated():
        return FftSampleRequester._sample_updated
