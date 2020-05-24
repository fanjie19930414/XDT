package com.kapok.apps.maple.xdt.timetable.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.ex.setVisible

/**
 *  课程表 选择课程页面Adapter
 *  fanjie
 */
class TimeTableChooseSubjectAdapter(private val context: Context, dataList: ArrayList<ClassChooseSubjectBean>) :
    BaseQuickAdapter<ClassChooseSubjectBean, BaseViewHolder>(R.layout.item_timetable_choose_subject, dataList) {

    override fun convert(helper: BaseViewHolder, item: ClassChooseSubjectBean?) {
        val ivCheckSelected = helper.getView<ImageView>(R.id.ivCheckSelected)
        val tvChooseSubject = helper.getView<TextView>(R.id.tvChooseSubject)
        val tvChooseTeacher = helper.getView<TextView>(R.id.tvChooseTeacher)
        val ivChooseRightArrow = helper.getView<ImageView>(R.id.ivChooseRightArrow)

        // 已选中
        if (item!!.isSelected) {
            ivCheckSelected.setImageResource(R.mipmap.chk_box_on)
            tvChooseSubject.setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
            tvChooseTeacher.isEnabled = true
            tvChooseTeacher.setVisible(true)
            ivChooseRightArrow.setVisible(true)
            // 老师名称
            if (item.teacherOutPutVOList.size > 0) {
                var teacherName = ""
                for (teacherList in item.teacherOutPutVOList) {
                    teacherName = teacherName + teacherList.teacherName + ","
                }
                tvChooseTeacher.text = teacherName.substring(0, teacherName.length - 1)
            } else {
                tvChooseTeacher.text = ""
            }
        } else {
            ivCheckSelected.setImageResource(R.mipmap.chk_box_off)
            tvChooseSubject.setTextColor(context.resources.getColor(R.color.text_xdt))
            tvChooseTeacher.isEnabled = false
            tvChooseTeacher.setVisible(false)
            ivChooseRightArrow.setVisible(false)
        }
        // 科目名称
        if (item.ownerType == 2) {
            tvChooseSubject.text = item.subjectName + "(自定义科目)"
            tvChooseTeacher.hint = "请设置代课老师"
        } else {
            tvChooseSubject.text = item.subjectName
        }
        // 选中点击事件
        helper.addOnClickListener(R.id.tvChooseSubject)
        helper.addOnClickListener(R.id.ivCheckSelected)
        helper.addOnClickListener(R.id.tvChooseTeacher)
    }
}