package com.kotlin.baselibrary.custom

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.kotlin.baselibrary.R
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.cancel_confirm_dialog.*

/**
 * 取消 确认弹窗
 */
class CancelConfirmDialog constructor(context: Context, themeResId: Int, var str: String, var colorStr: String) :
    Dialog(context, themeResId) {
    lateinit var listener: ClickConfirmListener
    private var confirmContent = "确认"

    interface ClickConfirmListener {
        fun confirm()
    }

    fun setOnClickConfirmListener(listener: ClickConfirmListener) {
        this.listener = listener
    }

    fun setConfirmContent(str: String) {
        confirmContent = str
        tvConfirm.text = confirmContent
    }

    init {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.cancel_confirm_dialog)
        tvContent.text = str
        tvConfirm.text = confirmContent
        if (colorStr.isNotEmpty()) {
            tvColorContent.setVisible(true)
            tvColorContent.text = colorStr
        } else {
            tvColorContent.setVisible(false)
        }
        setProperty()
        initListener()
    }

    private fun initListener() {
        // 取消
        tvCancel.setOnClickListener { dismiss() }
        // 确认
        tvConfirm.setOnClickListener { listener.confirm() }
    }

    private fun setProperty() {
        val window = window
        val lp = window!!.attributes
        val d = window.windowManager.defaultDisplay
        lp.dimAmount = 0.6f
        lp.width = d.width
        window.attributes = lp
        window.setGravity(Gravity.CENTER)
        // 设置点击外围消散
        this.setCanceledOnTouchOutside(true)
    }
}