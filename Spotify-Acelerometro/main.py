#!/usr/bin/python

from Acelerometro import *
import spotipy
import spotipy.util as util

def main():
    username = 'lucaskfreitas'
    client_id = 'fbd9312c3e1e4942ac05ef1012776736'
    client_secret = '9cbd2569c9654861a72883a8fe678aad'
    redirect_uri = 'http://localhost:8888/callback/'
    scope = 'user-modify-playback-state'
    token = util.prompt_for_user_token(username, scope, client_id, client_secret, redirect_uri)
    sp = spotipy.Spotify(auth=token)

    acelerometro = Acelerometro()
    leitura = acelerometro.aguarda_comando()
    if leitura == LeituraAcelerometro.INC_RIGHT:
        sp.next_track()
    elif leitura == LeituraAcelerometro.INC_LEFT:
    	sp.previous_track()

if __name__ == "__main__":
    main()