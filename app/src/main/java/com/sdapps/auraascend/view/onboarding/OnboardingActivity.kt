package com.sdapps.auraascend.view.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.databinding.ActivityOnboardingBinding
import com.sdapps.auraascend.view.home.HomeScreen
import com.sdapps.auraascend.core.NetworkTools.isNetworkConnected
import com.sdapps.auraascend.core.SharedPrefHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var spRef: SharedPrefHelper
    private  var currentUser : String? = null


    private lateinit var viewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        spRef = SharedPrefHelper(this)
        currentUser = Firebase.auth.currentUser?.uid
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

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
        viewModel.onBoardingResponses.observe(this){ responses ->
            if (responses != null) {
                if (isNetworkConnected(this)){
                    CoroutineScope(Dispatchers.IO).launch {
                        val taskOne = async { uploadResponsesToFirestore(responses) }
                        val taskTwo = async { updateInFRB() }
                        awaitAll(taskOne,taskTwo)

                        withContext(Dispatchers.Main){
                            showCompletion(currentUser.toString())
                        }

                    }
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadResponsesToFirestore(response:  MutableMap<String, List<String>>){
        val db = FirebaseFirestore.getInstance()
        val data = mapOf("onBoarding_preference" to response)

        db.collection("users").document(currentUser.toString())
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Onboarding responses uploaded ✅")
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Error uploading responses", e)
            }
    }


    private fun updateInFRB(){
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("users")
        val map = mapOf<String, Boolean>("onboardComplete" to true)
        if(currentUser != null){
            usersRef.child(currentUser!!).updateChildren(map).addOnFailureListener { err ->
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