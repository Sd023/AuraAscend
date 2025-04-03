package com.sdapps.auraascend.view.onboarding.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.R

class AnswerAdapter(private val moods: List<String>, private var state: Boolean,private val onSelect : (String) -> Unit) :
    RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val text: TextView = view.findViewById(R.id.answerView)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val mood = moods[position]
        holder.text.text = mood
        holder.checkBox.isChecked = state

        holder.itemView.setOnClickListener {
            state = !state
            notifyItemChanged(position)
            onSelect(mood)
        }
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onSelect(mood)
        }

    }

    override fun getItemCount(): Int = moods.size
}
