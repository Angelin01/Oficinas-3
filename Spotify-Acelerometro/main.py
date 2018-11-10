#!/usr/bin/python

from Accelerometer import *
from SpotifyClient import *

def main():
    # Token must be informed by the Android App
    token = 'BQBS4q_TrLnOT-PYYGIpeYRAEECsIhZsqEvalGpdZkznww3zM4rk6iAfggYTfPAthsVeTQsjJnJjjy_QTvb9oE3j_FuZjej_EE6WfhK40KOVZnC1HNaJENHyMwVBtOfISkdnK94PRD4JaBkzx7PLgfzEYoBWD4gQzWDm4bpCzA'
    spotify = SpotifyClient(token)

    accelerometer = Accelerometer()

    while True:
        reading = accelerometer.wait_for_movement()
        if reading == AccReading.INC_RIGHT:
            spotify.next_track()
        elif reading == AccReading.INC_LEFT:
    	    spotify.previous_track()

if __name__ == "__main__":
    main()