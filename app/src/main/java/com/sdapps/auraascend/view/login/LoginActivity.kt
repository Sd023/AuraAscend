package com.sdapps.auraascend.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.databinding.ActivityLoginBinding
import com.sdapps.auraascend.view.home.HomeScreen
import androidx.core.view.isGone
import com.sdapps.auraascend.view.login.register.RegisterUserActivity


class LoginActivity : AppCompatActivity(), LoginManager.View {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var presenter: LoginPresenter
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init(){
        progressDialog = CustomProgressDialog(this)
        presenter = LoginPresenter()
        mAuth = Firebase.auth
        presenter.attachView(this,applicationContext,mAuth)


        binding.loginBtn.setOnClickListener {
            val email = binding.etEmail.text?.trim().toString()
            val password = binding.etPassword.text?.trim().toString()

            presenter.login(email,password)
        }

        binding.continueAsGuestLayout.setOnClickListener {
            moveToNextScreen("guest")
        }

        binding.register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterUserActivity::class.java)
            startActivity(intent)
        }
    }


    fun expand(view : View){
        val v = if (binding.expandableContentLayout.isGone) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(binding.expandableContentLayout, AutoTransition())
        binding.expandableContentLayout.setVisibility(v)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            moveToNextScreen("user")
        }
    }
    override fun showLoading() {
       progressDialog.showPDialog()

    }

    override fun hideLoading() {
        progressDialog.closePdialog()
    }

    override fun moveToNextScreen(isFrom: String) {
        val intent = Intent(this@LoginActivity, HomeScreen::class.java)
        intent.putExtra("isBy", isFrom)
        startActivity(intent)
    }

    override fun showError(msg: String) {
        progressDialog.showAlert(msg)
    }
}