package com.kotlin.baselibrary.custom

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.blankj.utilcode.util.SnackbarUtils.dismiss
import com.kotlin.baselibrary.R
import kotlinx.android.synthetic.main.dialog_childsex_bottom.*

/**
 * 底部弹窗（孩子性别）
 */
class CustomBottomChildSexDialog constructor(context: Context, themeResId: Int,var identity: Int = 0) :
    Dialog(context, themeResId) {
    lateinit var isBoyorGirl: IsBoyorGirl

    interface IsBoyorGirl {
        fun chooseBoy(boolean: Boolean)
    }

    fun setIsBoyorGirl(isBoyorGirl: IsBoyorGirl) {
        this.isBoyorGirl = isBoyorGirl
    }

    fun setIdentityCode(identity: Int) {
        this.identity = identity
    }

    init {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.dialog_childsex_bottom)
        when (identity) {
            // 学生
            0 -> {
                tvDialogBoy.text = "男孩"
                tvDialogGirl.text = "女孩"
                ivDialogBoy.setImageResource(R.drawable.boy)
                ivDialogGirl.setImageResource(R.drawable.girl)
            }
            // 家长
            1 -> {
                tvDialogBoy.text = "男"
                tvDialogGirl.text = "女"
                ivDialogBoy.setImageResource(R.drawable.def_head_boy02)
                ivDialogGirl.setImageResource(R.drawable.def_head_girl02)
            }
            // 老师
            2 -> {
                tvDialogBoy.text = "男"
                tvDialogGirl.text = "女"
                ivDialogBoy.setImageResource(R.drawable.def_head_boy03)
                ivDialogGirl.setImageResource(R.drawable.def_head_girl03)
            }
        }
        setProperty()
        initListener()
    }

    private fun initListener() {
        // 取消
        tv_bottomdialogsex_cancel.setOnClickListener { dismiss() }
        // 男孩
        ll_dialog_boy.setOnClickListener {
            isBoyorGirl.chooseBoy(true)
            dismiss()
        }
        // 女孩
        ll_dialog_girl.setOnClickListener {
            isBoyorGirl.chooseBoy(false)
            dismiss()
        }
    }

    private fun setProperty() {
        val window = window
        val lp = window!!.attributes
        val d = window.windowManager.defaultDisplay
        lp.dimAmount = 0.3f
        lp.width = d.width
        window.attributes = lp
        window.setGravity(Gravity.BOTTOM)
        // 设置点击外围消散
        this.setCanceledOnTouchOutside(true)
    }
}