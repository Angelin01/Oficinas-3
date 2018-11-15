package com.tesseract.spotify

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.tesseract.R

class SpotifyListAdapter(private var SpotifyPlaylist: ArrayList<SpotifyPlaylist>, var listener: OnSpotifyPlaylistItemClickListener) : RecyclerView.Adapter<SpotifyListAdapter.SpotifyListHolder>() {

	interface OnSpotifyPlaylistItemClickListener {
		fun onItemClick(item: SpotifyPlaylist)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotifyListHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.spotify_playlist_item, parent, false)
		return SpotifyListHolder(view)
	}

	override fun getItemCount(): Int {
		return SpotifyPlaylist.size
	}

	override fun onBindViewHolder(holder: SpotifyListHolder, position: Int) {
		holder.bind(SpotifyPlaylist[position], listener)
	}

	fun updateList(newSpotifyPlaylistList: ArrayList<SpotifyPlaylist>) {
		removeAll()
		for (playlist: SpotifyPlaylist in newSpotifyPlaylistList) {
			insertItem(playlist)
		}
	}

	private fun insertItem(spotifyPlayList: SpotifyPlaylist) {
		SpotifyPlaylist.add(spotifyPlayList)
		notifyItemInserted(itemCount)
	}

	private fun removeAll() {
		SpotifyPlaylist.clear()
		notifyDataSetChanged()
	}

	class SpotifyListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bind(item: SpotifyPlaylist, listener: OnSpotifyPlaylistItemClickListener) = with(itemView) {
			with(itemView.findViewById<TextView>(R.id.textViewSpotifyPlaylistName)) {
				text = item.name
			}
			with(itemView.findViewById<TextView>(R.id.textViewSpotifyPlaylistMusicQuantity)) {
				text = item.music_quantity.toString()
			}
			with(itemView.findViewById<ImageView>(R.id.imageSpotifyPlaylist)) {
				Picasso.get().load(item.playlist_cover_URI).into(this)
			}

			itemView.setOnClickListener { listener.onItemClick(item) }
		}
	}

}