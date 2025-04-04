package com.sdapps.auraascend.view.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.view.login.data.Constants.Companion.LOGIN

class LoginPresenter: LoginManager.Presenter {

    private lateinit var mContext: Context
    private lateinit var mView: LoginManager.View
    private lateinit var auth: FirebaseAuth
    private lateinit var spRef : SharedPrefHelper

    override fun attachView(view: LoginManager.View, context: Context, mAuth: FirebaseAuth) {
        this.mView = view
        this.mContext = context
        this.auth = mAuth
        this.spRef = SharedPrefHelper(context)
    }

    override fun login(email: String, password: String) {
        mView.showLoading()
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { task ->
            mView.hideLoading()
            val userId = task.user?.uid
            if(userId != null){
                spRef.setCurrentUser(userId)
                spRef.setOnBoardCompleteForUser(false)
                spRef.setOnboardComplete(false)
                spRef.setUserLoginType(LOGIN)
                mView.moveToNextScreen(LOGIN)
            }
        }.addOnFailureListener { err ->
            mView.hideLoading()
            mView.showError(err.message.toString())
            err.printStackTrace() }
    }

}