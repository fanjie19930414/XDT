package com.kotlin.baselibrary.utils

import android.app.ProgressDialog
import android.content.Context
import android.text.TextUtils

object ProgressUtils {
    private var progressDialog: ProgressDialog? = null

    fun showLoadDialog(context: Context,msg: String, canCancel: Boolean) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setCanceledOnTouchOutside(false)
        }
        progressDialog!!.setCancelable(canCancel)
        progressDialog!!.setMessage(msg)
        if (!progressDialog!!.isShowing) {
            progressDialog!!.setOnCancelListener(null)
            progressDialog!!.show()
        }
    }

    /**
     * 关闭loading框
     */
    fun closeLoadDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }
}