package com.sdapps.auraascend.view.home.fragments.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.SharedPrefHelper


class StatsAdapter(val spRef: SharedPrefHelper,val items: ArrayList<StatsBO>) : RecyclerView.Adapter<StatsAdapter.StatsViewHolder>(){

    class StatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val statsIcon : ImageView = itemView.findViewById(R.id.imageIcon)
        val activityName : TextView = itemView.findViewById(R.id.statsActivityName)
        val activityAcheived : TextView = itemView.findViewById(R.id.statsAchieved)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatsAdapter.StatsViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.stats_item, parent, false)
        return StatsViewHolder(layout)
    }

    override fun onBindViewHolder(holder: StatsAdapter.StatsViewHolder, position: Int) {
        val achievement = items[position]
        holder.statsIcon.setImageDrawable(achievement.statsIcon)
        holder.activityName.text = achievement.statsName
        if(achievement.statsName.equals("Story Time")){
            holder.activityAcheived.text = "${achievement.statsAchievement} / ${spRef.getTotalStories()}"
        } else {
            holder.activityAcheived.text = "${achievement.statsAchievement}"
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}