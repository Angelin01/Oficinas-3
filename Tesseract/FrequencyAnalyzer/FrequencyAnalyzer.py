from time import sleep

import scipy.fftpack
from rpi_audio_levels import AudioLevels
import numpy as np

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


def calculateFFT(music_samples, chunk_size, using_scipy=True, sample_rate=44100):
	window = np.blackman(0)

	if len(music_samples) != len(window):
		window = np.blackman(len(music_samples)).astype(np.float32)

	samples_windowed = music_samples * window

	if using_scipy:
		fourier = scipy.fftpack.fft(samples_windowed)
		fourier = np.abs(fourier[:chunk_size])
		fourier = fourier * 2 / chunk_size
	else:
		fourier = gpu(samples_windowed)

	#print('chuck size ', chunk_size)
	#print('sample rate ', sample_rate)
	#print('bands intervals ', bands_intervals)
	#print('fourier ', fourier[:int(chunk_size / 2)])
	bands = customBand(fourier[:int(chunk_size / 2)], sample_rate, bands_intervals)
	return bands


def filterInvalidNumbers(levels):
	for index, value in enumerate(levels):
		if np.isinf(value):
			levels[index] = 0


def gpu(data):
	data_size = int(np.log2(len(data)))  # 2**10 = 1024

	last_index = int(2 ** (data_size - 1))

	bands_indexes = [[i, i + 1] for i in range(last_index)]  # maximum index is 2**(10-1)

	audio_levels = AudioLevels(data_size, len(bands_indexes))

	new_data = [float(i) for i in data]
	data = np.array(new_data, dtype=np.float32)

	levels, _, _ = audio_levels.compute(data, bands_indexes)
	filterInvalidNumbers(levels)
	return levels


def bands(fourier, n_bands, sample_rate):
	frequencies = []
	levels = []
	max_frequency = sample_rate // 2
	bandwidth = max_frequency // n_bands
	points = int(np.floor(len(fourier) // n_bands))
	for band in range(n_bands):
		low_frequency = band * bandwidth
		high_frequency = low_frequency + bandwidth

		frequencies.append((high_frequency + low_frequency) // 2)
		points_for_band = fourier[band * points: band * points + points]

		level = max(points_for_band)
		levels.append(level)

	return levels, frequencies


def customBand(fourier, sample_rate, band_frequencies):
	indexes = getIndexFromFrequency(len(fourier), sample_rate, band_frequencies)
	levels = []
	for band in indexes:
		points_for_band = fourier[band[0]: band[1]]
		level = max(points_for_band)
		levels.append(level)

	return levels


def getIndexFromFrequency(sample_size, sample_rate, bands_frequencies):
	frequency_spacing = (sample_rate / 2) / sample_size
	indexes = []
	off_set = 0
	for band in bands_frequencies:
		init_index = int(band[0] // frequency_spacing)
		end_index = int(np.floor(band[1] / frequency_spacing))
		if init_index == end_index:
			indexes.append([off_set + init_index, off_set + init_index+1])
			off_set += 1
		else:
			indexes.append([off_set + init_index, off_set + end_index])

	return indexes
