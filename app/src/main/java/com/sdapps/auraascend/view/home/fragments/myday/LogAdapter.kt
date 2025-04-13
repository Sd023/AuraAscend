package com.sdapps.auraascend.view.home.fragments.myday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.room.EmotionEntity

class LogAdapter(val moodList : List<EmotionEntity>, val mContext: Context): RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tvDate : TextView = itemView.findViewById<TextView>(R.id.tvDate)
        var tvUserInput : TextView = itemView.findViewById<TextView>(R.id.tvUserInput)
        var tvUserSelectedMood : TextView = itemView.findViewById<TextView>(R.id.tvUserSelectedMood)
        var tvUserSelectedCategories : TextView = itemView.findViewById<TextView>(R.id.tvUserSelectedCategories)
        var tvPredictedMood : TextView = itemView.findViewById<TextView>(R.id.tvPredictedMood)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.log_item,parent,false)
        return ViewHolder(layout)
    }

    private val labels = mapOf(
        0 to "sadness",
        1 to "joy",
        2 to "love",
        3 to "anger",
        4 to "fear",
        5 to "surprise"
    )

    override fun onBindViewHolder(holder: LogAdapter.ViewHolder, position: Int) {
        val mood = moodList[position]
        val color = when (mood.predictedMood) {
            "joy", "love" -> R.color.ForestGreen
            "anger", "fear", "sadness" -> R.color.Red
            "surprise" -> R.color.Olive
            else -> R.color.CornflowerBlue
        }

        holder.tvDate.text = buildString { append("Date: ").append(mood.date) }
        holder.tvUserInput.text = buildString { append("Message: ").append(mood.userInput) }
        holder.tvUserSelectedMood.text = buildString { append("Selected mood: ").append(mood.userSelectedMood) }
        holder.tvUserSelectedCategories.text = buildString { append("Factors: ").append(mood.userSelectedCategories) }

        holder.tvPredictedMood.setTextColor(ContextCompat.getColor(mContext, color))
        holder.tvPredictedMood.text = buildString { append("Predicted mood: ").append(mood.predictedMood) }
    }

    override fun getItemCount(): Int {
       return moodList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}