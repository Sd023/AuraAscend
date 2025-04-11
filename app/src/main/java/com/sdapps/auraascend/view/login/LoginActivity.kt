package com.sdapps.auraascend.view.login

import android.Manifest
import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.view.login.data.Constants.Companion.GUEST
import com.sdapps.auraascend.view.login.data.Constants.Companion.RC_SIGN_IN
import com.sdapps.auraascend.view.login.data.Constants.Companion.SIGNINWITHGOOGLE
import com.sdapps.auraascend.view.login.data.Constants.Companion.LOGIN
import com.sdapps.auraascend.view.login.googlesignin.GoogleSignInHelper
import com.sdapps.auraascend.view.login.register.RegisterUserActivity
import com.sdapps.auraascend.view.onboarding.OnboardingActivity


class LoginActivity : AppCompatActivity(), LoginManager.View {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var presenter: LoginPresenter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignHelper: GoogleSignInHelper
    private lateinit var spRef: SharedPrefHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("TAG", "granted")
        } else {
            askNotificationPermission()
        }
    }

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
        spRef = SharedPrefHelper(this)
        progressDialog = CustomProgressDialog(this)
        presenter = LoginPresenter()
        mAuth = Firebase.auth
        presenter.attachView(this,applicationContext,mAuth)
        googleSignHelper = GoogleSignInHelper(this)

        binding.loginBtn.setOnClickListener {
            val email = binding.etEmail.text?.trim().toString()
            val password = binding.etPassword.text?.trim().toString()

            presenter.login(email,password)
        }

        binding.continueAsGuestLayout.setOnClickListener {
            moveToNextScreen(GUEST, false)
        }


        binding.gSignIn.setOnClickListener {
            val signInIntent = googleSignHelper.client.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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

    override fun showLoading() {
       progressDialog.showPDialog()

    }

    override fun hideLoading() {
        progressDialog.closePialog()
    }


    override fun onResume() {
        super.onResume()
        val isFrom = spRef.getUserLoginType()
        if(spRef.getUserOnboardStatus()){
            callHomeScreen(isFrom)
        } else {
            val currentUser = mAuth.currentUser
            if(currentUser != null){
                startOnBoard(spRef.getUserLoginType())
            }
        }
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

    override fun moveToNextScreen(isFrom: String, isOnboardComplete: Boolean) {
        when(isFrom){
            LOGIN -> {
                if(spRef.getUserOnboardStatus() && isOnboardComplete){
                   callHomeScreen(isFrom)
                } else {
                    startOnBoard(isFrom)
                }

            }

            SIGNINWITHGOOGLE -> {
                if(spRef.getUserOnboardStatus()){
                   callHomeScreen(isFrom)
                } else{
                    startOnBoard(isFrom)
                }
            }
        }

    }

    private fun callHomeScreen(isFrom: String){
        val intent = Intent(this@LoginActivity, HomeScreen::class.java)
        intent.putExtra("isBy", isFrom)
        startActivity(intent)
        finish()
    }
    private fun startOnBoard(isFrom: String){
        val intent = Intent(this@LoginActivity, OnboardingActivity::class.java)
        intent.putExtra("isBy", isFrom)
        startActivity(intent)
        finish()
    }

    override fun showError(msg: String) {
        progressDialog.showAlert(msg)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if(user != null){
                        spRef.setCurrentUser(user.uid)
                        spRef.setOnboardComplete(false)
                        spRef.setOnBoardCompleteForUser(false)
                        spRef.setUserLoginType(SIGNINWITHGOOGLE)
                        moveToNextScreen(SIGNINWITHGOOGLE, false)
                    }
                } else {
                    Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}