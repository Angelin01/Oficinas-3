import soundfile as sf
import numpy as np
import scipy.fftpack
import matplotlib.pyplot as plt

class SoundAnalyzer():


	def __init__(self) -> None:
		super().__init__()
		self.plot_active = False
		self.NUMBER_OF_SAMPLES = 1024
		self.sample_rate = 44100

	def convertWAVToOGG(self, filename: str):
		data, self.sample_rate = sf.read(filename + '.wav')
		sf.write(filename + '.ogg', data, self.sample_rate)

	def runFile(self, filename: str):

		music_data, self.sample_rate = sf.read(filename)
		self.run(music_data=music_data)


	def run(self, music_data):

		# music_data = music_data[:, 0] # pega apenas um canal
		music_data = music_data[:] # pega apenas um canal

		sample_spacing = 1 / self.sample_rate

		for samples_position in range(0, len(music_data), self.NUMBER_OF_SAMPLES):

			music_samples = music_data[samples_position : samples_position + self.NUMBER_OF_SAMPLES]

			fourier_amplitudes = scipy.fftpack.fft(music_samples)
			fourier_points = np.linspace(0.0, 1.0 / (2.0*sample_spacing), self.NUMBER_OF_SAMPLES // 2)

			printable_x = fourier_points[:self.NUMBER_OF_SAMPLES // 2]
			printable_y = fourier_amplitudes[:self.NUMBER_OF_SAMPLES // 2]

			indexes_to_get = [1,2,5,8,10,12,15,18,20,25,30,35,40,50,60,75,90,100,150,200,250,300,375,450]
			printable_x = list(printable_x[indexes_to_get])
			printable_y = list(printable_y[indexes_to_get])

			self.plot(printable_x, printable_y)

	def plot(self, x_points, y_points):

		if self.plot_active is False:
			self.figure, self.axis = plt.subplots(1, 1)
			self.axis.hold(True)
			plt.show(block=False)
			plt.draw()
			self.background = self.figure.canvas.copy_from_bbox(self.axis.bbox)
			self.plot_active = True
			self.lines = self.axis.plot(x_points, 2.0 / self.NUMBER_OF_SAMPLES * np.abs(y_points[:self.NUMBER_OF_SAMPLES // 2]))[0]
			self.figure.canvas.draw()

		self.lines.set_data(x_points, 2.0 / self.NUMBER_OF_SAMPLES * np.abs(y_points[:self.NUMBER_OF_SAMPLES // 2]))

		self.figure.canvas.restore_region(self.background)

		# redraw just the self.SAMPLES
		self.axis.draw_artist(self.lines)

		# fill in the axes rectangle

		self.figure.canvas.blit(self.axis.bbox)
		plt.pause(0.0003)
