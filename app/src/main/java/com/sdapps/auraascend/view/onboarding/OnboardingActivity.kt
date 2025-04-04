package com.sdapps.auraascend.view.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.sdapps.auraascend.databinding.ActivityOnboardingBinding
import com.sdapps.auraascend.view.home.HomeScreen
import com.sdapps.auraascend.core.NetworkTools.isNetworkConnected
import com.sdapps.auraascend.core.SharedPrefHelper

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var spRef: SharedPrefHelper
    private  var currentUser : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        spRef = SharedPrefHelper(this)
        currentUser = Firebase.auth.currentUser?.uid

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = OnboardingPagerAdapter(this)

    }

    fun moveToNextPage() {
        val nextPage = binding.viewPager.currentItem + 1
        if (nextPage < 5) {
            binding.viewPager.setCurrentItem(nextPage, true)
        } else {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        Toast.makeText(this, "Onboarding Complete!", Toast.LENGTH_SHORT).show()
        if(isNetworkConnected(this)){
            updateInFRB()
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
            if(currentUser!!.isNotEmpty()){
                showCompletion(currentUser!!)
            }
        }
    }

    private fun updateInFRB(){
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("users")
        val map = mapOf<String, Boolean>("onboardComplete" to true)
        if(currentUser != null){
            usersRef.child(currentUser!!).updateChildren(map).addOnSuccessListener {
                showCompletion(currentUser!!)
            }.addOnFailureListener { err ->
                Toast.makeText(this,err.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showCompletion(uid: String){
        spRef.setCurrentUser(uid)
        spRef.setOnboardComplete(true)
        spRef.setOnBoardCompleteForUser(true)

        startActivity(Intent(this@OnboardingActivity, HomeScreen::class.java))
        finish()
    }
}