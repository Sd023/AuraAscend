package com.sdapps.auraascend.view.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        EmojiCompat.init(BundledEmojiCompatConfig(this))
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val guestMode = intent.extras?.getString("isBy") ?: ""
        init(guestMode)

    }

    private fun init(guestMode: String){
        val navView: BottomNavigationView = binding.navView
        if(guestMode == "guest") {
            navView.menu.findItem(R.id.nav_stats)?.isVisible = false
            navView.menu.findItem(R.id.nav_my_day)?.isVisible = false
            val navController = findNavController(R.id.nav_host_fragment_activity_main2)
            val navInflater = navController.navInflater
            val navGraph = navInflater.inflate(R.navigation.mobile_navigation)
            navGraph.setStartDestination(R.id.nav_activity)

            navController.graph = navGraph
            navView.setupWithNavController(navController)

        } else{
            val navController = findNavController(R.id.nav_host_fragment_activity_main2)
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_my_day, R.id.nav_activity, R.id.nav_stats))
            //setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }



}
