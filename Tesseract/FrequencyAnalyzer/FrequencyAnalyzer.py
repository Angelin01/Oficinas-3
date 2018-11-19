import scipy.fftpack
from rpi_audio_levels import AudioLevels
import numpy as np
from Audio import Audio


def calculateFFT(music_samples, chunk_size, n_bands=24, bands_intervals=[], using_scipy=True,
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
	n_bands, frequencies = bands(fourier, n_bands, sample_rate)

	return n_bands, frequencies


def gpu(data):
	print("Inside gpu function")
	DATA_SIZE = 11
	BANDS_COUNT = len(data)
	audio_levels = AudioLevels(DATA_SIZE, BANDS_COUNT)

	bands_indexes = [[i, i + 1] for i in range(Audio.chunk_size // 2)]
	new_data = []
	for i in data:
		new_data.append(float(i))

	data = np.array(new_data, dtype=np.float32)
	print("antes")
	levels, _, _ = audio_levels.compute(data, bands_indexes)
	print("Levels")
	print(levels)
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

		level = sum(points_for_band)
		levels.append(level)

	return levels, frequencies
