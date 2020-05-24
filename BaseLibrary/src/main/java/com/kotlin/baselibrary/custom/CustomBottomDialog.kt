package com.kotlin.baselibrary.custom

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kotlin.baselibrary.R
import com.kotlin.baselibrary.custom.adapter.CustomBottomDialogAdapter
import kotlinx.android.synthetic.main.dialog_bottom_custom.*
import kotlinx.android.synthetic.main.dialog_layout_item.*

/**
 * 底部弹窗工具类
 */
class CustomBottomDialog constructor(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private lateinit var selectTextListener: SelectTextListener
    private lateinit var selectItem: String
    private lateinit var dialogAdapter: CustomBottomDialogAdapter

    interface SelectTextListener {
        fun selectText(text: String)
    }

    fun setOnselectTextListener(selectTextListener: SelectTextListener) {
        this.selectTextListener = selectTextListener
    }


    init {
        initView()
    }

    fun setTitle(title: String) {
        tv_bottomdialog_title.text = title
    }

    /**
     * @param text 显示名称
     * @param listener 点击监听
     */
    fun addItem(text: String, listener: View.OnClickListener) {
        val child = View.inflate(context, R.layout.dialog_layout_item_default, null) as LinearLayout
        tv_dialog_layout.text = text
        layout_add.addView(child)
        child.setOnClickListener(listener)
    }

    /**
     * @param dataList 数据集合
     * @param selectText 已选中的文字
     */
    fun addItem(dataList: ArrayList<String>, selectText: String) {
        selectItem = selectText
        val recyclerView = RecyclerView(context)
        dialogAdapter = CustomBottomDialogAdapter(context, dataList)
        if (selectText.isNotEmpty()) {
            dialogAdapter.setSelectText(selectText)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dialogAdapter
        layout_add.addView(recyclerView)
        dialogAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                selectItem = adapter.getItem(position) as String
                (adapter as CustomBottomDialogAdapter).setSelectText(selectItem)
            }
    }

    private fun initView() {
        setContentView(R.layout.dialog_bottom_custom)
        setProperty()
        initListener()
    }

    private fun initListener() {
        // 取消
        tv_bottomdialog_cancel.setOnClickListener {
            dismiss()
        }
        // 确认
        tv_bottomdialog_confirm.setOnClickListener {
            selectTextListener.selectText(selectItem)
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