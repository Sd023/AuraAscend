package com.sdapps.auraascend.view.home.fragments.funactivity.storytime

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.databinding.StoryItemBinding

class StoryPagerAdapter(
    private val stories: List<StoryModel>,
    private val drawableList: ArrayList<Drawable>
) : RecyclerView.Adapter<StoryPagerAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.binding.storyTitle.text = story.title
        holder.binding.storyContent.text = story.content
        try {
            holder.binding.storyImage.setImageDrawable(drawableList[position])
        }catch (ex: Exception){
            ex.printStackTrace()
        }

    }
}
