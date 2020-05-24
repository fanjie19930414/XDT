package com.kotlin.baselibrary.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.util.Size
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kotlin.baselibrary.R
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.GlideUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_bottom_cancel.*

/**
 * 底部带取消的Dialog
 */
class CustomCancelBottomDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    init {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.dialog_bottom_cancel)
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
        window.setGravity(Gravity.BOTTOM)
        // 设置点击外围消散
        this.setCanceledOnTouchOutside(true)
    }

    private fun initListener() {
        tv_cancel_bottom.setOnClickListener { dismiss() }
    }

    /**
     *  设置标题
     */
    fun addTitle(text: String, textColor: Int) {
        val child = View.inflate(context, R.layout.dialog_layout_item_default, null) as LinearLayout
        val tvBottomDialog = child.findViewById<TextView>(R.id.tv_dialog_layout)
        tvBottomDialog.text = text
        tvBottomDialog.setTextColor(context.resources.getColor(textColor))
        tvBottomDialog.typeface = Typeface.DEFAULT_BOLD
        layout_add_cancel_bottom.addView(child)
        tvBottomDialog.setTextSize(TypedValue.COMPLEX_UNIT_SP,18.toFloat()) // 方法4
    }

    /**
     * @param text 显示名称
     * @param textColor 字体颜色
     * @param listener 点击监听
     */
    fun addItem(text: String, textColor: Int, listener: View.OnClickListener) {
        val child = View.inflate(context, R.layout.dialog_layout_item_default, null) as LinearLayout
        val tvBottomDialog = child.findViewById<TextView>(R.id.tv_dialog_layout)
        tvBottomDialog.text = text
        tvBottomDialog.setTextColor(context.resources.getColor(textColor))
        layout_add_cancel_bottom.addView(child)
        child.setOnClickListener(listener)
    }

    /**
     * @param text 显示名称
     * @param textColor 字体颜色
     * @param listener 点击监听
     */
    fun addChildItem(text: String, textColor: Int, listener: View.OnClickListener) {
        val child = View.inflate(context, R.layout.dialog_layout_item, null) as LinearLayout
        val tvBottomDialog = child.findViewById<TextView>(R.id.tv_dialog_layout)
        val civChildIcon = child.findViewById<CircleImageView>(R.id.civChildIcon)
        val addChildIcon = child.findViewById<ImageView>(R.id.addChildIcon)
        civChildIcon.setVisible(false)
        addChildIcon.setVisible(true)
        tvBottomDialog.text = text
        tvBottomDialog.setTextColor(context.resources.getColor(textColor))
        layout_add_cancel_bottom.addView(child)
        child.setOnClickListener(listener)
    }

    /**
     * 带头像的Item(用于班级列表 右上角 添加按钮)
     */
    fun addChildrenItem(text: String,textColor: Int,avatar: String,listener: View.OnClickListener) {
        val child = View.inflate(context, R.layout.dialog_layout_item, null) as LinearLayout
        val tvBottomDialog = child.findViewById<TextView>(R.id.tv_dialog_layout)
        val civChildIcon = child.findViewById<CircleImageView>(R.id.civChildIcon)
        val addChildIcon = child.findViewById<ImageView>(R.id.addChildIcon)
        civChildIcon.setVisible(true)
        addChildIcon.setVisible(false)
        // 头像
        if (avatar.isNotEmpty()) {
            GlideUtils.loadUrlImage(context,avatar,civChildIcon)
        } else {
            Glide.with(context).load(R.drawable.boy).into(civChildIcon)
        }
        // 姓名
        tvBottomDialog.text = text
        tvBottomDialog.setTextColor(context.resources.getColor(textColor))
        layout_add_cancel_bottom.addView(child)
        child.setOnClickListener(listener)
    }
}