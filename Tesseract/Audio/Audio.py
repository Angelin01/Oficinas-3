import pyaudio
import struct
import numpy as np

chunk_size = 1024 * 2
data_format = pyaudio.paInt16
n_channels = 2
sample_rate = 44100

p = pyaudio.PyAudio()

input_device_index = 1

stream = p.open(format=data_format,
                channels=n_channels,
                rate=sample_rate,
                input=True,
                frames_per_buffer=chunk_size,
                input_device_index=input_device_index)


def getLoopbackAudioData():
	raw_data = stream.read(chunk_size, exception_on_overflow=False)

	# Convert raw sound data to Numpy array
	fmt = "%dH" % (len(raw_data) / 2)
	data = struct.unpack(fmt, raw_data)
	data = np.array(data, dtype='h')

	return data


def stopLoopbackAudioStream():
	stream.stop_stream()
	stream.close()
	p.terminate()
