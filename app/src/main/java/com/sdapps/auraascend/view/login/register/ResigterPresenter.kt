package com.sdapps.auraascend.view.login.register

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sdapps.auraascend.view.login.LoginManager
import com.sdapps.auraascend.view.login.data.Constants.Companion.REGISTER
import com.sdapps.auraascend.view.login.data.UserBO

class RegisterPresenter: LoginManager.RegisterPresenter {
    private lateinit var mContext: Context
    private lateinit var mView: LoginManager.RegisterView
    private lateinit var auth: FirebaseAuth

    override fun attachView(
        view: LoginManager.RegisterView,
        context: Context,
        mAuth: FirebaseAuth
    ) {
        this.mView = view
        this.mContext = context
        this.auth = mAuth
    }

    override fun registerUser(email: String, password: String) {
    }

    override fun createUser(userBO: UserBO,password: String) {
        mView.showLoading()
        if(userBO.email.isNotEmpty() && userBO.email.isNotEmpty()){
            auth.createUserWithEmailAndPassword(userBO.email,password)
                .addOnSuccessListener {
                    userBO.userId = auth.currentUser?.uid.toString()
                    addUserToFrb(userBO)
                }.addOnFailureListener { err ->
                    mView.hideLoading()
                    mView.showError(err.message.toString())
                }
        } else {
            mView.showError("Email and Password is empty!")
        }
    }


    private fun addUserToFrb(userBO: UserBO){
        if(userBO.userId.isNotEmpty()){
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.reference.child("users")
            val map = mapOf<String, UserBO>(userBO.userId.toString() to userBO)

            usersRef.updateChildren(map).addOnSuccessListener { task ->
                mView.hideLoading()
                mView.showError("User Created Successfully!")
                mView.moveToNextScreen(REGISTER)
            }.addOnFailureListener { error ->
                mView.hideLoading()
                mView.showError(error.message.toString())
            }

        } else {
            mView.showError("Unable to create user in firebase!")
            return
        }

    }
}