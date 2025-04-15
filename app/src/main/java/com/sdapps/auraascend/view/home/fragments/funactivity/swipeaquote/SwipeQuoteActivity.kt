package com.sdapps.auraascend.view.home.fragments.funactivity.swipeaquote

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.databinding.ActivitySwipeQuoteBinding
import kotlin.getValue

class SwipeQuoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwipeQuoteBinding
    private lateinit var spRef : SharedPrefHelper
    private val dm: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySwipeQuoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dm.fetchQuote()
        spRef = SharedPrefHelper(this)
        init()
    }


    private fun init(){
        val bgColors = listOf(
            Color.parseColor("#FFE4E1"),
            Color.parseColor("#B0E0E6"),
            Color.parseColor("#FFF0F5"),
            Color.parseColor("#F5FFFA"),
            Color.parseColor("#F0FFF0"),
            Color.parseColor("#FFDAB9"),
            Color.parseColor("#EEE8AA"),
            Color.parseColor("#E0FFFF"),
            Color.parseColor("#FFFACD"),
            Color.parseColor("#FFB6C1"),
            Color.parseColor("#FFFFE0"),
            Color.parseColor("#F0F8FF"),
            Color.parseColor("#FFF5EE"),
            Color.parseColor("#FAF0E6"),
            Color.parseColor("#AFEEEE"),
            Color.parseColor("#D8BFD8"),
            Color.parseColor("#ADD8E6"),
            Color.parseColor("#E6E6FA"),
            Color.parseColor("#F0FFFF"),
            Color.parseColor("#F7E7CE")
        )
        val evaluator = ArgbEvaluator()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                if (position < bgColors.size - 1) {
                    val color = evaluator.evaluate(
                        positionOffset,
                        bgColors[position],
                        bgColors[position + 1]
                    ) as Int
                    binding.mainLayout.setBackgroundColor(color)
                } else {
                    binding.mainLayout.setBackgroundColor(bgColors.last())
                }
            }
        })

        dm.allQuotes.observe(this) { quotes ->
            if(quotes.isNotEmpty()){
                val adapter = SwipeQuoteAdapter(quotes,spRef)
                binding.viewPager.adapter = adapter
            }
        }
        dm.fetchAllQuotes()
    }
}