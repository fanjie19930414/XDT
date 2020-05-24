package com.kapok.apps.maple.xdt.timetable.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.ex.setVisible

/**
 * 根据班级id选择老师列表Adapter
 */
open class BottomTeacherListDialogAdapter(val context: Context, dataList: ArrayList<TeacherOutPutVOList>) :
    BaseQuickAdapter<TeacherOutPutVOList, BaseViewHolder>(R.layout.dialog_layout_item_teacher_list, dataList) {

    override fun convert(helper: BaseViewHolder, item: TeacherOutPutVOList) {
        val tvTeacherListItem = helper.getView<TextView>(R.id.tvTeacherListItem)
        val ivTeacherListItem = helper.getView<ImageView>(R.id.ivTeacherListItem)

        helper.addOnClickListener(R.id.rlTeaherListItem)
        if (item.isSelected) {
            tvTeacherListItem.setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
            tvTeacherListItem.text = item.teacherName
            ivTeacherListItem.setVisible(true)
        } else {
            tvTeacherListItem.setTextColor(context.resources.getColor(R.color.text_xdt))
            tvTeacherListItem.text = item.teacherName
            ivTeacherListItem.setVisible(false)
        }
    }
}