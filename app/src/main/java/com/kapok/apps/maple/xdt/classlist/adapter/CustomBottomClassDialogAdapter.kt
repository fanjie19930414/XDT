package com.kapok.apps.maple.xdt.classlist.adapter

import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList

/**
 * 底部弹窗工具类(用于老师班级详情页Adapter)
 */
open class CustomBottomClassDialogAdapter(val context: Context, dataList: MutableList<TeacherOutPutVOList>) :
    BaseQuickAdapter<TeacherOutPutVOList, BaseViewHolder>(R.layout.dialog_layout_class_item, dataList) {

    override fun convert(helper: BaseViewHolder, item: TeacherOutPutVOList) {
        if (item.isSelected) {
            helper.getView<TextView>(R.id.tv_dialog_layout).setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
        } else {
            helper.getView<TextView>(R.id.tv_dialog_layout).setTextColor(context.resources.getColor(R.color.text_xdt_hint))
        }
        helper.getView<TextView>(R.id.tv_dialog_layout).text = item.teacherName
    }
}