package com.sdapps.auraascend.view.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sdapps.auraascend.view.login.data.Constants.Companion.REGISTER
import com.sdapps.auraascend.view.login.data.Constants.Companion.USER
import com.sdapps.auraascend.view.login.data.UserBO

class LoginPresenter: LoginManager.Presenter {

    private lateinit var mContext: Context
    private lateinit var mView: LoginManager.View
    private lateinit var auth: FirebaseAuth

    override fun attachView(view: LoginManager.View, context: Context, mAuth: FirebaseAuth) {
        this.mView = view
        this.mContext = context
        this.auth = mAuth
    }

    override fun login(email: String, password: String) {
        mView.showLoading()
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { task ->
            mView.hideLoading()
            mView.moveToNextScreen(USER)
        }.addOnFailureListener { err ->
            mView.hideLoading()
            mView.showError(err.message.toString())
            err.printStackTrace() }
    }

}