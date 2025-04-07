package com.sdapps.auraascend.view.home.fragments.funactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.R
import com.sdapps.auraascend.view.home.fragments.funactivity.FunActivityFragment.Companion.COMING_SOON

class FunActivityAdapter(private val activityList: ArrayList<FunActivities>,private val onActivitySelected : (String) -> Unit) :
    RecyclerView.Adapter<FunActivityAdapter.FunViewHolder>() {

    class FunViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var activityName : TextView = view.findViewById<TextView>(R.id.activityName)
        var activityTagLine : TextView = view.findViewById<TextView>(R.id.activityTagLine)
        var activityIcon : ImageView = view.findViewById<ImageView>(R.id.activityIcon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FunViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.fun_activity_items,parent, false)
        return FunViewHolder(layout)
    }

    override fun onBindViewHolder(
        holder: FunViewHolder,
        position: Int
    ) {
        val actList = activityList[position]

        holder.activityIcon.setImageDrawable(actList.activityIcon)
        holder.activityName.text = actList.activityName
        holder.activityTagLine.text = actList.activityTagLine

        if(actList.activityCode.equals(COMING_SOON)) {
            holder.itemView.isClickable = false
            holder.itemView.isEnabled = false
        }

        holder.itemView.setOnClickListener {
            onActivitySelected(actList.activityCode)
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return activityList.size
    }


}