package com.kotlin.baselibrary.utils

import android.content.Context
import android.view.Gravity
import com.blankj.utilcode.util.ToastUtils
import com.kotlin.baselibrary.R

/**
 * Toast工具类
 */
object ToastUtils {
    fun showMsg(context: Context, msg: String) {
        ToastUtils.setGravity(Gravity.CENTER,0,0)
        ToastUtils.setBgColor(context.resources.getColor(R.color.black_70))
        ToastUtils.setMsgColor(context.resources.getColor(R.color.white))
        ToastUtils.showShort(msg)
    }
}