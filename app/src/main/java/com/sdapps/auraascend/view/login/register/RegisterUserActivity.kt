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
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.databinding.ActivityRegisterUserBinding
import com.sdapps.auraascend.view.login.data.Constants.Companion.RC_SIGN_IN
import com.sdapps.auraascend.view.login.data.Constants.Companion.REGISTER
import com.sdapps.auraascend.view.login.LoginActivity
import com.sdapps.auraascend.view.login.LoginManager
import com.sdapps.auraascend.view.login.data.UserBO
import com.sdapps.auraascend.view.login.googlesignin.GoogleSignInHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

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

        binding.edtDOB.attachDatePicker(supportFragmentManager)

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

           (uphone.isEmpty() || (uphone.length != 10)) -> {
                binding.etPhone.error = "Enter a valid phone number"
            }

            uemail.isNotValid() -> {
                binding.etEmail.error = "Please enter a valid email"
            }

            else -> {
                val userBO = UserBO().apply {
                    name = uname
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

    fun TextInputEditText.attachDatePicker(fragmentManager: FragmentManager) {
        isFocusable = false
        isClickable = true

        val dateValidator = CompositeDateValidator.allOf(listOf(maxDate())) // Max Date Allowed till today.

        val constraints: CalendarConstraints =
            CalendarConstraints.Builder()
                .setValidator(dateValidator)
                .build()


        val builder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .setTitleText("")
            .setTheme(R.style.MyDatePickerTheme)
            .setSelection(Calendar.getInstance().timeInMillis)

        val datePicker = builder.build()

        setOnClickListener {
            datePicker.show(fragmentManager, "DATE_PICKER_TAG")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            setText(format.format(date))
        }
    }

    private fun maxDate(): CalendarConstraints.DateValidator {
        // it allow user to show and choose current day.
        // need to add validation birth year and age.
        return DateValidatorPointBackward.before(Calendar.getInstance().timeInMillis)
    }

    private fun minDate(): CalendarConstraints.DateValidator {
        // it will allow user only from a selected day till current day in past.
        // below code is to allow user to choose 45 days before current day.
        // since its dob, we are not using it here. if needed can able.
        return DateValidatorPointForward.from(
            Calendar.getInstance().timeInMillis - 45.days.toLong(
                DurationUnit.MILLISECONDS
            )
        )
    }

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