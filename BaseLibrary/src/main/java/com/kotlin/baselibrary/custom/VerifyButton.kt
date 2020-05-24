package com.kotlin.baselibrary.custom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.Button
import com.kotlin.baselibrary.R

/**
 * @desciption: 获取验证码按钮，带倒计时
 */
class VerifyButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    private val mHandler: Handler = Handler()
    private var mCount = 60
    private var mOnVerifyBtnClick: OnVerifyBtnClick? = null

    /**
     * 倒计时，并处理点击事件
     */
    fun requestSendVerifyNumber() {
        mHandler.postDelayed(countDown, 0)
        if (mOnVerifyBtnClick != null) {
            mOnVerifyBtnClick!!.onClick()
        }
    }

    /**
     *  倒计时
     */
    @SuppressLint("SetTextI18n")
    private val countDown = object : Runnable {
        override fun run() {
            this@VerifyButton.text = "重新获取" + mCount.toString() + "秒 "
            this@VerifyButton.setTextColor(resources.getColor(R.color.text_xdt_hint))
            this@VerifyButton.isEnabled = false
            if (mCount > 0) {
                mHandler.postDelayed(this, 1000)
            } else {
                resetCounter()
            }
            mCount--
        }

    }

    fun removeRunable() {
        mHandler.removeCallbacks(countDown)
    }

    /**
     * 恢复到初始状态
     */
    private fun resetCounter(vararg txt: String) {
        this.isEnabled = true
        if (txt.isNotEmpty() && "" != txt[0]) {
            this.text = txt[0]
        } else {
            this.text = "重新获取"
        }
        this.setBackgroundColor(resources.getColor(R.color.transparent))
        this.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        mCount = 60
    }

    /**
     * 点击事件接口
     */
    interface OnVerifyBtnClick {
        fun onClick()
    }

    fun setOnVerifyBtnClick(onVerifyBtnClick: OnVerifyBtnClick) {
        this.mOnVerifyBtnClick = onVerifyBtnClick
    }
}