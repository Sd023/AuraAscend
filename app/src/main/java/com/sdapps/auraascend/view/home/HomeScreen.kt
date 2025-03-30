package com.sdapps.auraascend.view.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.ActivityHomeScreenBinding
import com.sdapps.auraascend.view.home.spotify.getSpotifyToken
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdapps.auraascend.view.home.spotify.FetchSong.fetchPodcasts

class HomeScreen : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("TAG", "granted")
        } else {
         askNotificationPermission()
        }
    }
    private lateinit var binding: ActivityHomeScreenBinding
    private var spotifyUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
    }


    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.d("TAG", "")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun init(){
        askNotificationPermission()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("TAG", token.toString())
            Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()


        })
       binding.myAdapter.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        getSpotifyToken { token ->
            if (token != null) {
                fetchPodcasts(token, "Higher Learning: Live With Thought Warriors") { podcasts ->
                    runOnUiThread {
                        podcasts?.let {
                            val adapter = PodcastAdapter(it) { spotifyUrl ->
                                openSpotify(spotifyUrl)
                            }
                            binding.myAdapter.adapter = adapter
                        } ?: Toast.makeText(this, "No Podcasts Found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Failed to get token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openSpotify(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.setPackage("com.spotify.music")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
}
