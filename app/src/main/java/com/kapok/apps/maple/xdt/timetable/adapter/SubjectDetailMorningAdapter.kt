package com.kapok.apps.maple.xdt.timetable.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.SubjectDetailBean
import com.kotlin.baselibrary.ex.setVisible

/**
 * 课程表详情Adapter
 * */
class SubjectDetailMorningAdapter(private var dataList: MutableList<SubjectDetailBean>) :
    BaseQuickAdapter<SubjectDetailBean, BaseViewHolder>(R.layout.item_subject_detail, dataList) {

    override fun convert(helper: BaseViewHolder, item: SubjectDetailBean) {
        val tvSubject = helper.getView<TextView>(R.id.tvSubject)
        val tvSubjectTeacher = helper.getView<TextView>(R.id.tvSubjectTeacher)
        val ivSubjectTel = helper.getView<ImageView>(R.id.ivSubjectTel)
        // 样式
        if (helper.layoutPosition == 0) {
            helper.getView<View>(R.id.viewUp).visibility = View.INVISIBLE
        } else if (helper.layoutPosition == dataList.size - 1) {
            helper.getView<View>(R.id.viewDown).visibility = View.INVISIBLE
        }
        // 数据
        if (item.timeTableDetailList != null) {
            tvSubject.text = item.timeTableDetailList.subjectName
            if (item.timeTableDetailList.teacherName != null) {
                ivSubjectTel.setVisible(true)
                tvSubjectTeacher.text = item.timeTableDetailList.teacherName + "老师"
            } else {
                ivSubjectTel.setVisible(false)
                tvSubjectTeacher.setVisible(false)
            }
        }
    }
}