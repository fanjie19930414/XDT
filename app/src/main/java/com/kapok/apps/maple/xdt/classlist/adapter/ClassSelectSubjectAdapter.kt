package com.kapok.apps.maple.xdt.classlist.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.ex.setVisible

/**
 * 班级列表模块选择老师Adapter
 */
class ClassSelectSubjectAdapter(val context: Context, dataList: MutableList<ClassChooseSubjectBean>) :
    BaseQuickAdapter<ClassChooseSubjectBean, BaseViewHolder>(R.layout.item_class_select_subject, dataList) {

    override fun convert(helper: BaseViewHolder, item: ClassChooseSubjectBean) {
        val tvClassListSubject = helper.getView<TextView>(R.id.tvClassListSubject)
        val ivClassListSubject = helper.getView<ImageView>(R.id.ivClassListSubject)
        helper.addOnClickListener(R.id.rlClassListSubject)

        if (item.isChoose) {
            ivClassListSubject.setVisible(true)
            tvClassListSubject.setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
        } else {
            ivClassListSubject.setVisible(false)
            tvClassListSubject.setTextColor(context.resources.getColor(R.color.text_xdt))
        }
        tvClassListSubject.text = item.subjectName
    }
}