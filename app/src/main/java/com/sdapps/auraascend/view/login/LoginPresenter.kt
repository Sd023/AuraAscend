package com.sdapps.auraascend.view.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.core.room.AppDAO
import com.sdapps.auraascend.core.room.AppDatabase
import com.sdapps.auraascend.view.login.data.Constants.Companion.LOGIN

class LoginPresenter: LoginManager.Presenter {

    private lateinit var mContext: Context
    private lateinit var mView: LoginManager.View
    private lateinit var auth: FirebaseAuth
    private lateinit var spRef : SharedPrefHelper
    private lateinit var dataVm: DataViewModel
    private lateinit var dao: AppDAO

    override fun attachView(view: LoginManager.View, context: Context, mAuth: FirebaseAuth, vm: DataViewModel) {
        this.mView = view
        this.mContext = context
        this.auth = mAuth
        this.spRef = SharedPrefHelper(context)
        this.dataVm = vm
        dao = AppDatabase.getDatabase(context).getAppDAO()
    }

    override fun login(email: String, password: String) {
        mView.showLoading()
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener { task ->
            mView.hideLoading()
            val userId = task.user?.uid
            if(userId != null){
                downloadQuotes()
                spRef.setCurrentUser(userId)
                getOnBoardStatus(userId)
            }
        }.addOnFailureListener { err ->
            mView.hideLoading()
            mView.showError(err.message.toString())
            err.printStackTrace()
        }
    }

    override fun downloadQuotes() {
        dataVm.downloadAllQuotes(dao)
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