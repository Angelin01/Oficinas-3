package com.tesseract

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), MainActivity.StatusChanged {

    private lateinit var musicController: MusicController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)


        musicController = activity?.run { ViewModelProviders.of(this).get(MusicController::class.java) }!!
        updateMusicInformation(musicController.music!!, view, musicController)

        val buttonNext: ImageButton = view.findViewById(R.id.buttonPlayNext)
        buttonNext.setOnClickListener {
            musicController.next()
            musicController.play()
            this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
            Log.d("TAG", "button next pressed")

        }

        val buttonPlay: ImageButton = view.findViewById(R.id.buttonPlay)
        buttonPlay.setOnClickListener {
            musicController.playToggle()
            this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
            Log.d("TAG", "button play pressed")

        }

        val buttonPrevious: ImageButton = view.findViewById(R.id.buttonPlayPrevious)
        buttonPrevious.setOnClickListener {
            musicController.previous()
            musicController.play()
            this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
            Log.d("TAG", "button previous pressed")

        }

        val buttonShuffle: ImageButton = view.findViewById(R.id.buttonPlayShuffle)
        buttonShuffle.setOnClickListener {
            musicController.shuffleToggle()
            this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
            Log.d("TAG", "button shuffle pressed")
        }

        val buttonVolume: ImageButton = view.findViewById(R.id.buttonVolume)
        val seekVolume: SeekBar = view.findViewById(R.id.seekVolume)
        seekVolume.visibility = View.INVISIBLE
        buttonVolume.setOnClickListener {
            this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
            this.showVolumeSlider(this.view!!)
            Log.d("TAG", "button volume pressed")
        }

        val seekBar: SeekBar = view.findViewById(R.id.seekMusicProgress)
        seekBar.setOnTouchListener { _, _ -> true }

        return view
    }

    override fun onStart() {
        super.onStart()

        this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
        this.updateBluetoothStatus()
    }

    override fun onResume() {
        super.onResume()

        this.updateMusicInformation(musicController.music!!, this.view!!, musicController)
        this.updateBluetoothStatus()
    }


    private fun updateMusicInformation(music: Music, view: View, musicController: MusicController) {
        val musicCoverView: ImageView = view.findViewById(R.id.imageViewMusicCover)
        Log.i("TAG", music.album_cover_url)
        Picasso.get().load(music.album_cover_url).into(musicCoverView)

        val musicNameView: TextView = view.findViewById(R.id.textViewMusicName)
        musicNameView.text = music.name

        val bandNameView: TextView = view.findViewById(R.id.textViewMusicBand)
        bandNameView.text = music.band_name

        val seekVolume: SeekBar = view.findViewById(R.id.seekVolume)
        seekVolume.progress = music.volume

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
        if (musicController.shuffle) {
            buttonShuffle.setImageResource(R.drawable.ic_play_shuffle_on)
        } else {
            buttonShuffle.setImageResource(R.drawable.ic_play_shuffle)
        }
    }

    private fun updateButtonPlay(view: View, musicController: MusicController) {
        val buttonPlay: ImageButton = view.findViewById(R.id.buttonPlay)
        if (musicController.playing) {
            buttonPlay.setImageResource(R.drawable.ic_pause)
        } else {
            buttonPlay.setImageResource(R.drawable.ic_play)
        }
    }

    private fun updateBluetoothStatus() {

        if (home_bluetooth_status == null){
            return
        }
        if (BluetoothController.bluetoothService!!.mState.equals(BluetoothService.STATE_CONNECTED)) {
            home_bluetooth_status.setBackgroundColor(context!!.getColor(R.color.secondaryColor))
        } else {
            home_bluetooth_status.setBackgroundColor(context!!.getColor(R.color.colorAccent))
        }
    }

    override fun onStatusChange(connected: Boolean) {
        updateBluetoothStatus()
    }

}
