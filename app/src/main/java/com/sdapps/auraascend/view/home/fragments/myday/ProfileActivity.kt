package com.sdapps.auraascend.view.home.fragments.myday

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.databinding.ActivityProfileBinding
import com.sdapps.auraascend.view.login.LoginActivity
import com.sdapps.auraascend.view.login.data.UserBO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: DataViewModel by viewModels()
    private lateinit var sharedPref : SharedPrefHelper
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPrefHelper(this)
        progressDialog = CustomProgressDialog(this)
        viewCreate()
    }

    private fun viewCreate(){
        progressDialog.showLoadingProgress("Fetching Profile..")
        lifecycleScope.launch {
            delay(500)
            viewModel.profileBO.collect { userBO ->
                withContext(Dispatchers.Main){
                    init(userBO)
                }
            }
        }

        viewModel.fetchProfileConfigs(sharedPref.getCurrentUser())
    }


    private fun init(userBO: UserBO){
        progressDialog.closePialog()

        binding.btnLogout.setOnClickListener {
            progressDialog.showAlert("Do you want to log-out?", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Firebase.auth.signOut()
                    clearSP()
                }

            })
        }
        if(userBO != null){
            binding.tvName.text = userBO.userName
            binding.tvPhone.text = userBO.phoneNumber
            binding.tvDob.text = userBO.dateOfBirth
            binding.tvEmail.text = userBO.email
        } else {
            progressDialog.showAlert("Unable to fetch profile!")
        }
    }

    private fun clearSP(){
        sharedPref.clearSp()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
        finish()
    }
}