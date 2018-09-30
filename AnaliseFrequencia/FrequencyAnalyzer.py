import scipy.fftpack
from rpi_audio_levels import AudioLevels
import numpy as np

class FrequencyAnalyzer:

	def __init__(self):
		pass

	@staticmethod
	def calculateFFT(music_samples, using_scipy=True):
		if using_scipy:

			return scipy.fftpack.fft(music_samples)

		levels = FrequencyAnalyzer.gpu(music_samples)
		return levels

	@staticmethod
	def gpu(data):
		DATA_SIZE = 11
		BANDS_COUNT = 1024
		audio_levels = AudioLevels(DATA_SIZE, BANDS_COUNT)

		window = np.hanning(0)

		if len(data) != len(window):
			window = np.hanning(len(data)).astype(np.float32)

		data = data * window

		bands_indexes = [[i, i+1] for i in range(1024)]
		new_data = []
		for i in data:
			new_data.append(float(i))

		data = np.array(new_data, dtype=np.float32)
		levels, means, stds = audio_levels.compute(data, bands_indexes)
		return levels