package com.kapok.apps.maple.xdt.homework.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import java.util.ArrayList

/**
 *  选择班级Adapter
 */
class HomeWorkChooseClassAdapter(private val context: Context, dataList: MutableList<TeacherInClasses>) :
    BaseQuickAdapter<TeacherInClasses, BaseViewHolder>(R.layout.item_homework_chooseclass, dataList) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: TeacherInClasses) {
        val ivHomeWorkIsChoose = helper.getView<ImageView>(R.id.ivHomeWorkIsChoose)
        val tvHomeWorkClassName = helper.getView<TextView>(R.id.tvHomeWorkClassName)
        val tvHomeWorkClassGrade = helper.getView<TextView>(R.id.tvHomeWorkClassGrade)
        val tvHomeWorkHaveChoose = helper.getView<TextView>(R.id.tvHomeWorkHaveChoose)
        helper.addOnClickListener(R.id.ivHomeWorkIsChoose)
        // 是否选中
        if (item.isChoose) {
            ivHomeWorkIsChoose.setImageResource(R.mipmap.chk_box_on)
            tvHomeWorkHaveChoose.setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
        } else {
            ivHomeWorkIsChoose.setImageResource(R.mipmap.chk_box_off)
            tvHomeWorkHaveChoose.setTextColor(context.resources.getColor(R.color.text_xdt_hint))
        }
        // 名称
        tvHomeWorkClassName.text = item.grade + " " + item.className + "班"
        // 入学年份
        tvHomeWorkClassGrade.text = "(" + item.startYear.toString() + "级)"
        // 选中学生数量 选中老师数量
        if (item.chooseTeacherNum != 0) {
            tvHomeWorkHaveChoose.text = "已选中学生" + item.chooseStudentNum + "人" + ",已选中老师" + item.chooseTeacherNum + "人"
        } else {
            tvHomeWorkHaveChoose.text = "已选中学生" + item.chooseStudentNum + "人"
        }
    }
}