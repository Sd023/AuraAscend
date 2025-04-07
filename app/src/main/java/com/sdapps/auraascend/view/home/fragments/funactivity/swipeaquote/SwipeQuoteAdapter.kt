package com.sdapps.auraascend.view.home.fragments.funactivity.swipeaquote

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.DayQuotes
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.SwipeaquoteItemBinding

class SwipeQuoteAdapter(private val quotesList: ArrayList<DayQuotes>):  RecyclerView.Adapter<SwipeQuoteAdapter.QuoteViewHolder>() {

    class QuoteViewHolder(binding: SwipeaquoteItemBinding): RecyclerView.ViewHolder(binding.root) {
        var quoteText : TextView = binding.quoteText
        var likeIcon : ImageView = binding.like
        var bookmarkIcon : ImageView = binding.bookmark

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuoteViewHolder {
        val binding = SwipeaquoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quotes = quotesList[position]
        holder.quoteText.text = quotes.quote
        var isLiked = false
        var isBookMarked = false

        holder.likeIcon.setOnClickListener {
            isLiked = !isLiked
            holder.likeIcon.setImageResource(if(isLiked) R.drawable.ic_like else R.drawable.ic_like_outline)
        }

        holder.bookmarkIcon.setOnClickListener {
            isBookMarked = !isBookMarked
            holder.bookmarkIcon.setImageResource(if(isBookMarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline)
        }
    }

    override fun getItemCount(): Int {
       return quotesList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }




}