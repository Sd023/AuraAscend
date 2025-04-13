package com.sdapps.auraascend.core

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.sdapps.auraascend.R

class CustomProgressDialog(private val appContext: Context) {

    private var progressDialog: AlertDialog? = null
    private var pDialog : ProgressDialog? = null


    fun showAlert(msg: String){
        progressDialog = AlertDialog.Builder(appContext)
            .setMessage(msg)
            .setPositiveButton("OK"
            ) { dialog, _ -> dialog?.dismiss() }
            .setCancelable(false)
            .show()
    }

    fun showAlert(msg: String, listener : DialogInterface.OnClickListener){
        progressDialog = AlertDialog.Builder(appContext)
            .setMessage(msg)
            .setPositiveButton("OK",listener)
            .setCancelable(false)
            .show()
    }
    fun showPDialog(){
        pDialog = ProgressDialog(appContext)
        pDialog?.setTitle("Please wait")
        pDialog?.setMessage("Logging in...")
        pDialog?.setCancelable(false)
        pDialog?.show()

    }

    fun showLoadingProgress(msg: String){
        pDialog = ProgressDialog(appContext)
        pDialog?.setTitle("Please wait")
        pDialog?.setMessage(msg)
        pDialog?.setCancelable(false)
        pDialog?.show()

    }

    fun closePialog(){
        try {
            if(pDialog!!.isShowing){
                pDialog!!.dismiss()
            }
        }catch (ex: Exception){
            ex.printStackTrace()
        }
    }
}