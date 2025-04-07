package com.sdapps.auraascend.view.home.fragments.funactivity.storytime

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.ActivityStoryTimeBinding

class StoryTimeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStoryTimeBinding

    private val stories: List<StoryModel> by lazy {
        val gson = Gson()
        val type = object : TypeToken<List<StoryModel>>() {}.type
        gson.fromJson(loadJSONFromAssets(this,"stories.json"), type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }


    private fun init(){
        val drawableList = arrayListOf<Drawable>(
            ContextCompat.getDrawable(this, R.drawable.ic_fries)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_library_ghost)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_pancake)!!,
            ContextCompat.getDrawable(this, R.drawable.dino_champ)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_uno)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_wentbad)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_delivery_slip)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_slow_down)!!,
            ContextCompat.getDrawable(this, R.drawable.old_wheels)!!,
            ContextCompat.getDrawable(this, R.drawable.rain_check)!!,
        )

        val adapter = StoryPagerAdapter(stories,drawableList)
        var isLiked = false

        binding.storyPager.adapter = adapter

        binding.storyPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.storyCount.text = "Story : ${String.format("%02d/10", position + 1)}"
                binding.likeIcon.setOnClickListener {
                    isLiked = !isLiked
                    binding.likeIcon.setImageResource(if(isLiked) R.drawable.ic_like else R.drawable.ic_like_outline)
                }
            }
        })
    }

    fun loadJSONFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
