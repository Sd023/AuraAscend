package com.sdapps.auraascend.view.home.fragments.funactivity.spotify

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.ActivitySpotifyBinding
import com.sdapps.auraascend.view.home.PodcastAdapter
import com.sdapps.auraascend.view.home.spotify.PodcastShow
import kotlin.getValue

class SpotifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpotifyBinding
    private var itemList : ArrayList<PodcastShow>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySpotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            itemList = intent.getParcelableArrayListExtra("pod_list", PodcastShow::class.java)
        } else {
            itemList = intent.getParcelableArrayListExtra("pod_list")
        }

        if(itemList != null){
            init()
        } else {
            showToast("Unable to fetch podcasts!")
        }
    }

    private fun init(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val adapter = PodcastAdapter(itemList!!){
            Log.d("Clicked", it)
        }
        binding.recyclerView.adapter = adapter
    }


    private fun showToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}