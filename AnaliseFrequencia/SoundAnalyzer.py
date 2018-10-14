import numpy as np
import matplotlib.pyplot as plt

from FrequencyAnalyzer import FrequencyAnalyzer

class SoundAnalyzer():


	def __init__(self, number_of_samples) -> None:
		super().__init__()
		self.plot_active = False
		self.chunk_size = number_of_samples
		self.sample_rate = 44100
		self.sample_spacing = 1 / self.sample_rate

	def run(self, music_data):
		n_bands = 24
		fourier_amplitudes, frequencies = FrequencyAnalyzer.calculateFFT(music_data, self.chunk_size, n_bands=n_bands, using_scipy=True, sample_rate=self.sample_rate)
		y = 2.0 / self.chunk_size * np.abs(fourier_amplitudes)
		self.plot(frequencies, y)

	def plot(self, x_points, y_points):
		if self.plot_active is False:
			plt.ion()
			self.figure, self.axis = plt.subplots()
			self.figure.canvas.draw()
			self.background = self.figure.canvas.copy_from_bbox(self.axis.bbox)
			self.plot_active = True

			self.lines = self.axis.scatter(x_points, y_points)
			# self.lines = self.axis.plot(x_points, y_points)[0]

			plt.xlim(0, 22000)

		self.lines.set_offsets(np.c_[x_points, y_points])
		# self.lines.set_data(x_points, y_points)

		self.figure.canvas.restore_region(self.background)
		self.axis.draw_artist(self.lines)
		self.figure.canvas.draw()
		plt.pause(0.0001)