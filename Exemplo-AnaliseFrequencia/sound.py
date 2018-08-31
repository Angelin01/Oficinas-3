import soundfile as sf
import numpy as np
import scipy.fftpack
import matplotlib.pyplot as plt

file_name = '440hz'

data, samplarate = sf.read(file_name + '.wav')
sf.write(file_name + '.ogg', data, samplarate)

points = 1024

# rms = [np.sqrt(np.mean(block**2)) for block in
	# sf.blocks('sample_sound.ogg' , blocksize=points, overlap=512)]
music_data, samplerate = sf.read(file_name + '.ogg')

# music_data = music_data[:, 0] # pega apenas um canal
music_data = music_data[:] # pega apenas um canal


sample_rate = 44100

# sample spacing
sample_spacing = 1 / sample_rate

x = np.linspace(0.0, points*sample_spacing, points)	

fig, ax = plt.subplots(1, 1)
ax.hold(True)
plt.show(block=False)
plt.draw()
background = fig.canvas.copy_from_bbox(ax.bbox)
# tic = time.time()


points_to_plot = 0
i = 0

while points_to_plot < len(music_data):
	# y = np.sin(50.0 * 2.0*np.pi*x) + 0.5*np.sin(80.0 * 2.0*np.pi*x)
	y = music_data[points_to_plot : points_to_plot + points]
	yf = scipy.fftpack.fft(y)
	xf = np.linspace(0.0, 1.0/(2.0*sample_spacing), points/2)

	printable_x = xf[:points//2]
	printable_y = yf[:points//2]

	indexes_to_get = [1,2,5,8,10,12,15,18,20,25,30,35,40,50,60,75,90,100,150,200,250,300,375,450]
	printable_x = list(printable_x[indexes_to_get])
	printable_y = list(printable_y[indexes_to_get])
	
	if (points_to_plot == 0):
		lines = ax.plot(printable_x, 2.0/points * np.abs(printable_y[:points//2]))[0]
		fig.canvas.draw()
	else:
		lines.set_data(printable_x, 2.0/points * np.abs(printable_y[:points//2]))
	
	fig.canvas.restore_region(background)

            # redraw just the points
	ax.draw_artist(lines)

            # fill in the axes rectangle

	fig.canvas.blit(ax.bbox)
	# plt.savefig('frame{i}.png'.format(i=i))
	plt.pause(0.0003)
	# plt.cla()
	points_to_plot += points
	i += 1
