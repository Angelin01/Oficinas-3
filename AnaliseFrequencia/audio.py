import pyaudio
from SoundAnalyzer import SoundAnalyzer
import struct
import numpy as np


chunk_size = 1024 * 2
data_format = pyaudio.paInt16
n_channels = 2
sample_rate = 44100

p = pyaudio.PyAudio()

input_device_index = 2

stream = p.open(format=data_format,
				channels=n_channels,
				rate=sample_rate,
				input=True,
				frames_per_buffer=chunk_size,
				input_device_index=input_device_index)

print("**Listening**")

frames = []
soundAnalyzer = SoundAnalyzer(chunk_size)
while(True):
	data = stream.read(chunk_size, exception_on_overflow=False)

	# Convert raw sound data to Numpy array
	fmt = "%dH" % (len(data) / 2)
	data2 = struct.unpack(fmt, data)
	data2 = np.array(data2, dtype='h')
	soundAnalyzer.run(data2)

stream.stop_stream()
stream.close()
p.terminate()
