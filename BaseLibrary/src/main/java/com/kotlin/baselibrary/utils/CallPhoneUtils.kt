package com.kotlin.baselibrary.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 拨打电话
 */
object CallPhoneUtils {
    fun callPhone(context: Context, phoneStr: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneStr")
        intent.data = data
        context.startActivity(intent)
    }
}