package com.sdapps.auraascend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sdapps.auraascend.databinding.ActivityMainBinding



class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}