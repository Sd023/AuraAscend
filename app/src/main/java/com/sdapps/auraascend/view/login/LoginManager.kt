package com.sdapps.auraascend.view.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.sdapps.auraascend.view.login.data.UserBO

interface LoginManager {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun moveToNextScreen(isFrom: String,isOnboardComplete : Boolean)
        fun showError(msg: String)
    }

    interface Presenter {
        fun attachView(view: View, context: Context, mAuth: FirebaseAuth)
        fun login(email:String, password: String)
    }

    interface RegisterView {
        fun showLoading()
        fun hideLoading()
        fun moveToNextScreen(isFrom: String)
        fun showError(msg: String)
    }

    interface RegisterPresenter {
        fun attachView(view: RegisterView, context: Context, mAuth: FirebaseAuth)
        fun registerUser(email:String,password:String)
        fun createUser(userBO: UserBO)
    }


}