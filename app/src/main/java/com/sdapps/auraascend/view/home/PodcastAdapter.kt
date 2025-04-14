package com.sdapps.auraascend.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdapps.auraascend.R
import com.sdapps.auraascend.view.home.spotify.PodcastShow


class PodcastAdapter(
    private val podcasts: ArrayList<PodcastShow>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    class PodcastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.songTitle)
        val image: ImageView = view.findViewById(R.id.songImage)
        val songDesc: TextView = view.findViewById(R.id.songDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_podcast, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val podcast = podcasts[position]
        holder.title.text = podcast.name
        holder.songDesc.text = podcast.description

        Glide.with(holder.itemView.context)
            .load(podcast.images.firstOrNull()?.url)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onClick(podcast.externalUrls.spotify)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount() = podcasts.size
}
