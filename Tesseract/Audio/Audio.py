import struct
import numpy as np

import alsaaudio as alsa

chunk_size = 1024
data_format = alsa.PCM_FORMAT_S16_LE
n_channels = 1
sample_rate = 44100

stream = alsa.PCM(alsa.PCM_CAPTURE, device='hw:1,1')
stream.setchannels(n_channels)
stream.setrate(sample_rate)
stream.setperiodsize(chunk_size)


def getLoopbackAudioData():
	length, raw_data = stream.read()
	if length < chunk_size:
		return np.zeros(chunk_size)

	# Convert raw sound data to Numpy array
	fmt = "%dH" % (len(raw_data) / 2)
	data = struct.unpack(fmt, raw_data)
	data = np.array(data, dtype='h')

	return data


def stopLoopbackAudioStream():
	stream.close()
