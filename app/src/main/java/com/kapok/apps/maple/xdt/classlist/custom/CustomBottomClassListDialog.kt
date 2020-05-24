package com.kapok.apps.maple.xdt.classlist.custom

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.adapter.CustomBottomClassDialogAdapter
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import kotlinx.android.synthetic.main.dialog_bottom_class_custom.*

/**
 * 底部弹窗工具类(用于老师班级详情页)
 */
class CustomBottomClassListDialog constructor(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private lateinit var selectIndexListener: SelectIndexListener
    private var selectIndex: Int = -1
    private lateinit var dialogAdapter: CustomBottomClassDialogAdapter

    interface SelectIndexListener {
        fun selectIndex(position: Int)
    }

    fun setOnselectIndexListener(selectIndexListener: SelectIndexListener) {
        this.selectIndexListener = selectIndexListener
    }

    init {
        initView()
    }

    fun setTitle(title: String) {
        tv_bottomdialog_title.text = title
    }

    /**
     * @param dataList 数据集合
     * @param selectText 已选中的文字
     */
    fun addItem(dataList: MutableList<TeacherOutPutVOList>) {
        val recyclerView = RecyclerView(context)
        dialogAdapter = CustomBottomClassDialogAdapter(context, dataList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = dialogAdapter
        layout_add.addView(recyclerView)
        dialogAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                selectIndex = position
                dataList[position].isSelected = true
                dialogAdapter.notifyDataSetChanged()
            }
    }

    private fun initView() {
        setContentView(R.layout.dialog_bottom_class_custom)
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
            selectIndexListener.selectIndex(selectIndex)
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