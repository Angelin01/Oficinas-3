package com.tesseract

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.tesseract.bluetooth.BluetoothController
import com.tesseract.bluetooth.BluetoothService
import com.tesseract.bluetooth.BluetoothStatusChangeCallback
import com.tesseract.music.Music
import com.tesseract.music.MusicController
import com.tesseract.music.Player
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), BluetoothStatusChangeCallback {

    private lateinit var musicController: MusicController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)


        musicController = activity?.run { ViewModelProviders.of(this).get(MusicController::class.java) }!!
	    musicController.music.observe(activity!!, Observer<Music> { music ->
		    updateMusicInformation(music!!, view)
	    })
	    musicController.player.observe(activity!!, Observer<Player> { player ->
		    updatePlayerInformation(player!!, view, musicController)
	    })

        val buttonNext: ImageButton = view.findViewById(R.id.buttonPlayNext)
        buttonNext.setOnClickListener {
            musicController.next()
            musicController.play()
            Log.d("TAG", "button next pressed")

        }

        val buttonPlay: ImageButton = view.findViewById(R.id.buttonPlay)
        buttonPlay.setOnClickListener {
            musicController.playToggle()
            Log.d("TAG", "button play pressed")

        }

        val buttonPrevious: ImageButton = view.findViewById(R.id.buttonPlayPrevious)
        buttonPrevious.setOnClickListener {
            musicController.previous()
            musicController.play()
            Log.d("TAG", "button previous pressed")

        }

        val buttonShuffle: ImageButton = view.findViewById(R.id.buttonPlayShuffle)
        buttonShuffle.setOnClickListener {
            musicController.shuffleToggle()
            Log.d("TAG", "button shuffle pressed")
        }

        val buttonVolume: ImageButton = view.findViewById(R.id.buttonVolume)
        val seekVolume: SeekBar = view.findViewById(R.id.seekVolume)
        seekVolume.visibility = View.INVISIBLE
        buttonVolume.setOnClickListener {
            this.showVolumeSlider(this.view!!)
            Log.d("TAG", "button volume pressed")
        }

        val seekBar: SeekBar = view.findViewById(R.id.seekMusicProgress)
        seekBar.setOnTouchListener { _, _ -> true }

        return view
    }

    override fun onStart() {
        super.onStart()

        this.updateMusicInformation(musicController.music.value!!, this.view!!)
	    updatePlayerInformation(musicController.player.value!!, this.view!!, musicController)
	    this.updateBluetoothStatus()
    }

    override fun onResume() {
        super.onResume()

        this.updateMusicInformation(musicController.music.value!!, this.view!!)
	    updatePlayerInformation(musicController.player.value!!, this.view!!, musicController)
	    this.updateBluetoothStatus()
    }


    private fun updateMusicInformation(music: Music, view: View) {
	    Log.d("TAG", "Updatin Music informations")

	    val musicCoverView: ImageView = view.findViewById(R.id.imageViewMusicCover)
        Picasso.get().load(music.album_cover_url).into(musicCoverView)

        val musicNameView: TextView = view.findViewById(R.id.textViewMusicName)
        musicNameView.text = music.name

        val bandNameView: TextView = view.findViewById(R.id.textViewMusicBand)
        bandNameView.text = music.band_name

    }

	private fun updatePlayerInformation(player: Player, view: View, musicController: MusicController) {
		Log.d("TAG", "Updatin player informations")
		val seekVolume: SeekBar = view.findViewById(R.id.seekVolume)
		seekVolume.progress = player.volume

        updateButtonShuffle(view, musicController)
        updateButtonPlay(view, musicController)

	}

    private fun showVolumeSlider(view: View) {
        val seekVolume: SeekBar = view.findViewById(R.id.seekVolume)
        if (seekVolume.visibility == View.VISIBLE) {
            seekVolume.visibility = View.INVISIBLE
        } else {
            seekVolume.visibility = View.VISIBLE
        }
    }

    private fun updateButtonShuffle(view: View, musicController: MusicController) {
        val buttonShuffle: ImageButton = view.findViewById(R.id.buttonPlayShuffle)
        if (musicController.player.value!!.shuffle) {
            buttonShuffle.setImageResource(R.drawable.ic_play_shuffle_on)
        } else {
            buttonShuffle.setImageResource(R.drawable.ic_play_shuffle)
        }
    }

    private fun updateButtonPlay(view: View, musicController: MusicController) {
        val buttonPlay: ImageButton = view.findViewById(R.id.buttonPlay)
        if (musicController.player.value!!.playing) {
            buttonPlay.setImageResource(R.drawable.ic_pause)
        } else {
            buttonPlay.setImageResource(R.drawable.ic_play)
        }
    }

    private fun updateBluetoothStatus() {
        if (home_bluetooth_status == null) {
            return
        }

        if (BluetoothController.bluetoothService!!.mState == BluetoothService.BluetoothStates.STATE_CONNECTED) {
            home_bluetooth_status.setBackgroundColor(context!!.getColor(R.color.secondaryColor))
            textViewBluetoothConnection.text = "Tesseract Connected"
        } else {
            home_bluetooth_status.setBackgroundColor(context!!.getColor(R.color.colorAccent))
            textViewBluetoothConnection.text = "Tesseract Disconnected"
        }
    }

    override fun onStatusChange(connected: Boolean) {
        updateBluetoothStatus()
    }
}
