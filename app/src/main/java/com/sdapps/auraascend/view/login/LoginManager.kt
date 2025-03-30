package com.sdapps.auraascend.view.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

interface LoginManager {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun moveToNextScreen(isFrom: String)
        fun showError(msg: String)
    }

    interface Presenter {
        fun attachView(view: View, context: Context, mAuth: FirebaseAuth)
        fun login(email:String, password: String)
    }


}