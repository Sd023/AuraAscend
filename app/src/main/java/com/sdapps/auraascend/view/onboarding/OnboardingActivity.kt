package com.sdapps.auraascend.view.onboarding

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.ActivityOnboardingBinding

class   OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        updateInFRB()
    }

    private fun updateInFRB(){
        val currentUser = Firebase.auth.currentUser
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("users")
        val map = mapOf<String, Boolean>("isOnboardComplete" to true)
        if(currentUser != null){
            usersRef.child(currentUser.uid).updateChildren(map).addOnSuccessListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { err ->

            }
        }
    }
}