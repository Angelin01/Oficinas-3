import alsaaudio as alsa
import struct

import numpy as np

chunk_size = 256 #1024
data_format = alsa.PCM_FORMAT_S16_LE
n_channels = 1
sample_rate = 44100

available_loopbacks = ['hw:Loopback,1,0', 'hw:Loopback,1,1']

def tryGetLoopback():
	print('try get loopback')

	slist = []
	for device in available_loopbacks:
		print('device', device)
		try:
			stream = alsa.PCM(alsa.PCM_CAPTURE, device=device)
			stream.setchannels(n_channels)
			stream.setrate(sample_rate)
			stream.setperiodsize(chunk_size)
			stream.setformat(data_format)

			slist.append(stream)
		except Exception as exception:
			print(exception)
			pass

	return slist


stream_list = tryGetLoopback()


def getLoopbackAudioData():
	music_sample_1 = getSampleAudio()
	music_sample_2 = getSampleAudio()
	return np.append(music_sample_1, music_sample_2)


def getSampleAudio():
	for stream in stream_list:
		length = 0
		while length < chunk_size:
			length, raw_data = stream.read()

		# Convert raw sound data to Numpy array
		fmt = "%dH" % (len(raw_data) / 2)
		data = struct.unpack(fmt, raw_data)
		data = np.array(data, dtype='h')

		if np.count_nonzero(data) > 0:
			return data
		empty_return = data

	return empty_return


def stopLoopbackAudioStream():
	for stream in stream_list:
		stream.close()
