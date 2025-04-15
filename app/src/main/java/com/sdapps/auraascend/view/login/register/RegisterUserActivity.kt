package com.sdapps.auraascend.view.login.register

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.databinding.ActivityRegisterUserBinding
import com.sdapps.auraascend.view.login.data.Constants.Companion.RC_SIGN_IN
import com.sdapps.auraascend.view.login.data.Constants.Companion.REGISTER
import com.sdapps.auraascend.view.login.LoginActivity
import com.sdapps.auraascend.view.login.LoginManager
import com.sdapps.auraascend.view.login.data.UserBO
import com.sdapps.auraascend.view.login.googlesignin.GoogleSignInHelper

class RegisterUserActivity : AppCompatActivity(), LoginManager.RegisterView {

    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var presenter: RegisterPresenter
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignHelper: GoogleSignInHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
    }

    private fun init() {
        auth = Firebase.auth
        googleSignHelper = GoogleSignInHelper(this)

        progressDialog = CustomProgressDialog(this)
        presenter = RegisterPresenter()
        presenter.attachView(this, applicationContext, Firebase.auth)
        binding.btnSignup.setOnClickListener {
            validateAndCreateUser()
        }

        /*binding.gSignIn.setOnClickListener {
            val signInIntent = googleSignHelper.client.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }*/
    }

    private fun validateAndCreateUser() {
        clearErrors()

        val uname = binding.etName.editText?.text?.trim().toString()
        val upassword = binding.etPassword.editText?.text?.trim().toString()
        val udob = binding.etDob.editText?.text?.trim().toString()
        val uphone = binding.etPhone.editText?.text?.trim().toString()
        val uemail = binding.etEmail.editText?.text?.trim().toString()

        when {
            uname.isEmpty() -> {
                binding.etName.error = "Name is mandatory"
            }

            upassword.length < 6 -> {
                binding.etPassword.error = "Invalid Password, please use atleast 6 characters"
            }

            udob.isEmpty() -> {
                binding.etDob.error = "Date of birth is mandatory"
            }

            uphone.isEmpty() -> {
                binding.etPhone.error = "Phone is mandatory"
            }

            uemail.isNotValid() -> {
                binding.etEmail.error = "Please enter a valid email"
            }

            else -> {
                val userBO = UserBO().apply {
                    name = uname
                    password = upassword
                    dateOfBirth = udob
                    phoneNumber = uphone
                    email = uemail
                }
                createUser(userBO)
            }
        }
    }

    private fun String.isNotValid(): Boolean {
        return this.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


    private fun createUser(userBO: UserBO) {
        presenter.createUser(userBO)
    }

    private fun clearErrors() {
        binding.etName.error = null
        binding.etPassword.error = null
        binding.etDob.error = null
        binding.etPhone.error = null
        binding.etEmail.error = null
    }

    override fun showLoading() {
        progressDialog.showPDialog()
    }

    override fun hideLoading() {
        progressDialog.closePialog()
    }

    override fun moveToNextScreen(isFrom: String) {
        when (isFrom) {
            REGISTER -> {
                startActivity(Intent(this@RegisterUserActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    /*override fun onActivityResult(
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
    }*/

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun showError(msg: String) {
        progressDialog.showAlert(msg)
    }
}