import numpy as np
# import matplotlib.pyplot as plt

from FrequencyAnalyzer import FrequencyAnalyzer
from Audio import Audio

class SoundAnalyzer():

	def __init__(self, number_of_samples) -> None:
		super().__init__()
		self.plot_active = False
		self.chunk_size = number_of_samples
		self.sample_rate = Audio.sample_rate
		self.sample_spacing = 1 / self.sample_rate

	def getAmplitudes(self, music_data, plot=False):
		n_bands = 20
		fourier_amplitudes, frequencies = FrequencyAnalyzer.calculateFFT(music_data, self.chunk_size,
		                                                                 n_bands=n_bands,
		                                                                 using_scipy=False,
		                                                                 sample_rate=self.sample_rate)
		return fourier_amplitudes