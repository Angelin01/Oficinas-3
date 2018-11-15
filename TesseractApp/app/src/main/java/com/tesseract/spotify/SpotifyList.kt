package com.tesseract


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.tesseract.spotify.SpotifyController
import com.tesseract.spotify.SpotifyListAdapter
import com.tesseract.spotify.SpotifyPlaylist


class SpotifyList : Fragment(), SpotifyListAdapter.OnSpotifyPlaylistItemClickListener {
	override fun onItemClick(item: SpotifyPlaylist) {
		Toast.makeText(this.context, "Spotify Playlist clicked", Toast.LENGTH_SHORT).show()
	}

	private fun updateSpotifyPlaylistList(spotifyPlaylist: ArrayList<SpotifyPlaylist>) {
		if (activity != null) {
			activity!!.runOnUiThread {
				spotifyPlayListAdapter.updateList(spotifyPlaylist)
			}
		}
	}

	private val clickListener: SpotifyListAdapter.OnSpotifyPlaylistItemClickListener = this
	private lateinit var spotifyPlayListAdapter: SpotifyListAdapter
	private lateinit var spotifyController: SpotifyController
	private lateinit var recyclerViewSpotifyPlaylist: RecyclerView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.fragment_spotify_list, container, false)

		spotifyController = activity?.run { ViewModelProviders.of(this).get(SpotifyController::class.java) }!!

		initiateRecyclerView(view)



		val buttonPlaylistSearch: Button = view.findViewById(R.id.buttonPlaylistSearch)
		buttonPlaylistSearch.setOnClickListener {
			val editTextPlaylistSearch: EditText = view.findViewById(R.id.editTextPlaylistSearch)
			var textSearch: String = editTextPlaylistSearch.text.toString()
			textSearch = textSearch.trim()
			if (textSearch.isEmpty()) {
				textSearch = null.toString()
			}
			spotifyController.searchPlaylist(textSearch)
		}

		return view
	}

	private fun initiateRecyclerView(view: View) {
		recyclerViewSpotifyPlaylist = view.findViewById(R.id.recyclerViewSpotifyPlayist)

		spotifyPlayListAdapter = SpotifyListAdapter(spotifyController.spotifyPlayList.value as ArrayList<SpotifyPlaylist>, clickListener)
		recyclerViewSpotifyPlaylist.adapter = spotifyPlayListAdapter

		spotifyController.spotifyPlayList.observe(activity!!, Observer<List<SpotifyPlaylist>> { spotifyPlaylists ->
			updateSpotifyPlaylistList(spotifyPlaylists as ArrayList<SpotifyPlaylist>)
		})
		spotifyController.searchPlaylist(null)

		val layoutManager = LinearLayoutManager(this.context)
		recyclerViewSpotifyPlaylist.layoutManager = layoutManager

		recyclerViewSpotifyPlaylist.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
	}
}
