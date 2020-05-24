package com.kapok.apps.maple.xdt.homework.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.usercenter.bean.JoinClassBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.utils.GlideUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_class_detail_teacher.*
import kotlinx.android.synthetic.main.activity_user_info.*

/**
 *  选中班级下的学生列表Adapter
 */
class HomeWorkClassStudentAdapter(private val context: Context, dataList: MutableList<StudentInClasses>) :
    BaseQuickAdapter<StudentInClasses, BaseViewHolder>(R.layout.item_homework_classstudent, dataList) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: StudentInClasses) {
        val ivHomeWorkChooseStudent = helper.getView<ImageView>(R.id.ivHomeWorkChooseStudent)
        val civIconHomeWorkStudent = helper.getView<CircleImageView>(R.id.civIconHomeWorkStudent)
        val tvHomeWorkStudentName = helper.getView<TextView>(R.id.tvHomeWorkStudentName)
        // 是否选中
        if (item.isChoose) {
            ivHomeWorkChooseStudent.setImageResource(R.mipmap.chk_box_on)
        } else {
            ivHomeWorkChooseStudent.setImageResource(R.mipmap.chk_box_off)
        }
        // 头像
        if (item.studentAvatar != null && item.studentAvatar.isNotEmpty()) {
            GlideUtils.loadImage(context, item.studentAvatar, civIconHomeWorkStudent)
        } else {
            Glide.with(context).load(R.mipmap.def_head_boy).into(civIconHomeWorkStudent)
        }
        // 姓名
        tvHomeWorkStudentName.text = item.studentName
    }
}