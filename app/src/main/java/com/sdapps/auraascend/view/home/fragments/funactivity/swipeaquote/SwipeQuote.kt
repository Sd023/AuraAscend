package com.sdapps.auraascend.view.home.fragments.funactivity.swipeaquote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.databinding.FragmentSwipeQuoteBinding
import com.sdapps.auraascend.view.home.fragments.OnBackPress
import kotlin.math.abs

class SwipeQuote : Fragment() {


    private lateinit var _binding: FragmentSwipeQuoteBinding
    private val binding get() = _binding

    private val dm: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dm.fetchQuote()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSwipeQuoteBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transformer = ViewPager2.PageTransformer { page, position ->
            page.translationX = -position * page.width
            page.alpha = 1 - abs(position)
            page.scaleY = 0.85f + (1 - abs(position)) * 0.15f
        }


        dm.allQuotes.observe(viewLifecycleOwner) { quotes ->
            if(quotes.isNotEmpty()){
                val adapter = SwipeQuoteAdapter(quotes)
                binding.quotePager.adapter = adapter
                binding.quotePager.setPageTransformer(transformer)
            }
        }

        dm.fetchAllQuotes()
    }
}