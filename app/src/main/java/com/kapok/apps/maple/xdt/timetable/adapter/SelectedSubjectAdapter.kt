package com.kapok.apps.maple.xdt.timetable.adapter

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean

/**
 * 课程表科目Adapter
 * fanjie
 */
class SelectedSubjectAdapter(private val context: Context, dataList: MutableList<ClassChooseSubjectBean>) :
    BaseQuickAdapter<ClassChooseSubjectBean, BaseViewHolder>(R.layout.item_select_subject, dataList) {

    // 判断当前是否为选中状态
    private var selectIndex: Int = -1

    // 选中的课程接口回调
    private lateinit var selectLessonTimeInterface: SelectItemInterface

    interface SelectItemInterface {
        fun onSelectData(subject: ClassChooseSubjectBean?)
    }

    fun setSelectLessonTimeListener(selectInterface: SelectItemInterface) {
        this.selectLessonTimeInterface = selectInterface
    }

    override fun convert(helper: BaseViewHolder, item: ClassChooseSubjectBean?) {
        // 数据
        val llSelectSubject = helper.getView<LinearLayout>(R.id.llSelectSubject)
        val tvSelectSubject = helper.getView<TextView>(R.id.tvSelectSubject)
        val tvSelectTeacher = helper.getView<TextView>(R.id.tvSelectTeacher)
        // 科目名称
        tvSelectSubject.text = item?.subjectName
        // 教师 （有多个展示括号）
        val teacherList = item?.teacherOutPutVOList
        if (teacherList != null && teacherList.size > 0) {
            if (teacherList.size > 1) {
                tvSelectTeacher.text = teacherList[0].teacherName + "()"
            } else {
                tvSelectTeacher.text = teacherList[0].teacherName
            }
        }

        // 判断是否点击
        if (selectIndex == helper.layoutPosition) {
            item?.isChoose = true
            llSelectSubject.isSelected = true
            tvSelectSubject.setTextColor(context.resources.getColor(R.color.common_white))
            tvSelectTeacher.setTextColor(context.resources.getColor(R.color.common_white))
        } else {
            item?.isChoose = false
            llSelectSubject.isSelected = false
            tvSelectSubject.setTextColor(context.resources.getColor(R.color.text_xdt))
            tvSelectTeacher.setTextColor(context.resources.getColor(R.color.text_xdt_hint))
        }

        llSelectSubject.setOnClickListener {
            if (selectIndex == helper.layoutPosition) {
                selectIndex = -1
                selectLessonTimeInterface.onSelectData(null)
                notifyDataSetChanged()
            } else {
                selectIndex = helper.layoutPosition
                item?.isChoose = true
                llSelectSubject.isSelected = true
                selectLessonTimeInterface.onSelectData(item)
                notifyDataSetChanged()
            }
        }
    }
}