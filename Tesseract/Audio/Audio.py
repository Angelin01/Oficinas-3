import struct
import numpy as np

import alsaaudio as alsa

chunk_size = 1024
data_format = alsa.PCM_FORMAT_S16_LE
n_channels = 1
sample_rate = 44100

stream = alsa.PCM(alsa.PCM_CAPTURE, device='hw:Loopback,1,1')
stream.setchannels(n_channels)
stream.setrate(sample_rate)
stream.setperiodsize(chunk_size)
stream.setformat(data_format)

def getLoopbackAudioData():
	length = 0
	while length < chunk_size:
		length, raw_data = stream.read()

	# raw_data = raw_data[:chunk_size]
	# Convert raw sound data to Numpy array
	fmt = "%dH" % (len(raw_data) / 2)
	data = struct.unpack(fmt, raw_data)
	data = np.array(data, dtype='h')
	return data


def stopLoopbackAudioStream():
	stream.close()
