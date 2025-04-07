package com.sdapps.auraascend.view.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
                getOnBoardStatus(userId)
            }
        }.addOnFailureListener { err ->
            mView.hideLoading()
            mView.showError(err.message.toString())
            err.printStackTrace()
        }
    }

    private fun getOnBoardStatus(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("users").child(userId)
        usersRef.child("onboardComplete").get().addOnSuccessListener { dataSnapshot ->
           val isStatusComplete =  dataSnapshot.getValue(Boolean::class.java) ?: false

            spRef.setOnBoardCompleteForUser(isStatusComplete)
            spRef.setOnboardComplete(isStatusComplete)
            spRef.setUserLoginType(LOGIN)
            mView.moveToNextScreen(LOGIN, isStatusComplete)

        }.addOnFailureListener { err ->
            mView.showError(err.message.toString())

        }
    }

}