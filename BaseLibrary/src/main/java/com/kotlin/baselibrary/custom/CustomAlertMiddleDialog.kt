package com.kotlin.baselibrary.custom

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import com.kotlin.baselibrary.R
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.alert_dialog_middle_layout.*

/**
 * 弹窗Dialog
 */
class CustomAlertMiddleDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    init {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.alert_dialog_middle_layout)
        setProperty()
        initListener()
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

    private fun initListener() {
        btn_alert_dialog_cancel.setOnClickListener { dismiss() }
    }

    /**
     * @param text 显示名称
     * @param textColor 字体颜色
     * @param listener 点击监听
     */
    fun addItem(text: String, textColor: Int, title: String, subTitle: String, listener: View.OnClickListener) {
        btn_alert_dialog_ok.text = text
        btn_alert_dialog_ok.setTextColor(context.resources.getColor(textColor))
        tv_alert_dialog_message.text = title
        if (subTitle.isNotEmpty()) {
            tv_alert_dialog_sub_message.setVisible(true)
            tv_alert_dialog_sub_message.text = subTitle
        } else {
            tv_alert_dialog_sub_message.setVisible(false)
        }
        btn_alert_dialog_ok.setOnClickListener(listener)
    }
}