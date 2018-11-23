import alsaaudio as alsa
import struct

import numpy as np

chunk_size = 1024
data_format = alsa.PCM_FORMAT_S16_LE
n_channels = 1
sample_rate = 44100

available_loopbacks = ['hw:Loopback,1,1', 'hw:Loopback,1,0']

def tryGetLoopback():
	for device in available_loopbacks:
		try:
			stream = alsa.PCM(alsa.PCM_CAPTURE, device=device)
			return stream
		except Exception as exception:
			pass


stream = tryGetLoopback()

stream.setchannels(n_channels)
stream.setrate(sample_rate)
stream.setperiodsize(chunk_size)
stream.setformat(data_format)

def getLoopbackAudioData():
	music_sample_1 = getSampleAudio()
	music_sample_2 = getSampleAudio()
	return np.append(music_sample_1, music_sample_2)


def getSampleAudio():
	length = 0
	while length < chunk_size:
		length, raw_data = stream.read()

	# Convert raw sound data to Numpy array
	fmt = "%dH" % (len(raw_data) / 2)
	data = struct.unpack(fmt, raw_data)
	data = np.array(data, dtype='h')
	return data


def stopLoopbackAudioStream():
	stream.close()
