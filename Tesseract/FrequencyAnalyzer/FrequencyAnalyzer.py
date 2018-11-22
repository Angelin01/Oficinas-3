import scipy.fftpack
from rpi_audio_levels import AudioLevels
import numpy as np
from Audio import Audio

bands_intervals = [
	[0, 31],
	[31, 44],
	[44, 68],
	[68, 88],
	[88, 125],
	[125, 180],
	[180, 250],
	[250, 355],
	[355, 500],
	[500, 710],
	[710, 1000],
	[1000, 1400],
	[1400, 2000],
	[2000, 2800],
	[2800, 4000],
	[4000, 5600],
	[5600, 8000],
	[8000, 11300],
	[11300, 16000],
	[16000, 22000]]

def calculateFFT(music_samples, chunk_size, n_bands=20, using_scipy=True,
                 sample_rate=Audio.sample_rate):
	window = np.hanning(0)

	if len(music_samples) != len(window):
		window = np.hanning(len(music_samples)).astype(np.float32)

	samples_windowed = music_samples * window

	if using_scipy:
		fourier = scipy.fftpack.fft(samples_windowed)
	else:
		fourier = gpu(samples_windowed)

	fourier = np.abs(fourier[:chunk_size // 2])
	fourier = fourier * 2 / chunk_size
	n_bands = bands(fourier, chunk_size // 2, sample_rate, bands_intervals)

	return n_bands


def gpu(data):
	DATA_SIZE = 11
	BANDS_COUNT = len(data)
	audio_levels = AudioLevels(DATA_SIZE, BANDS_COUNT)

	bands_indexes = [[i, i + 1] for i in range(Audio.chunk_size*2)]
	new_data = []
	for i in data:
		new_data.append(float(i))

	data = np.array(new_data, dtype=np.float32)
	levels, _, _ = audio_levels.compute(data, bands_indexes)
	return levels

def bands(fourier, sample_size, sample_rate, bands_intervals):
	indexes = getIndexFromFrequency(sample_size, sample_rate, bands_intervals)
	levels = []
	for band in indexes:
		points_for_band = max(fourier[band[0]: band[1]])

		level = sum(points_for_band)
		levels.append(level)

	return levels

def getIndexFromFrequency(sample_size, sample_rate, bands_frequencies):
	frequency_spacing = (sample_rate / 2) / sample_size
	indexes = []
	for band in bands_frequencies:
		init_index = int(band[0] // frequency_spacing)
		end_index = int(np.floor(band[1] / frequency_spacing))
		indexes.append([init_index, end_index])

	return indexes