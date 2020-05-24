package com.kotlin.baselibrary.utils

import android.content.Context

object Dp2pxUtils {
    fun dp2px(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5).toInt()
    }
}