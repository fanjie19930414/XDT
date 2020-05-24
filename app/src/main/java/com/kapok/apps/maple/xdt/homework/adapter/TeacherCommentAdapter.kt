package com.kapok.apps.maple.xdt.homework.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.TeacherCommentParentBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.utils.GlideUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_check_homework_teacher.*

/**
 *  教师评论Adapter
 */
@SuppressLint("SetTextI18n")
class TeacherCommentAdapter(private val context: Context, dataList: MutableList<TeacherCommentParentBean>) :
    BaseQuickAdapter<TeacherCommentParentBean, BaseViewHolder>(R.layout.item_teacher_comment, dataList) {

    override fun convert(helper: BaseViewHolder, item: TeacherCommentParentBean) {
        val civCheckHomeWorkTeacherIcon = helper.getView<CircleImageView>(R.id.civTeacherComment)
        // 头像
        if (item.teacherAvatar != null && item.teacherAvatar.isNotEmpty()) {
            GlideUtils.loadImage(
                context,
                item.teacherAvatar,
                civCheckHomeWorkTeacherIcon
            )
        } else {
            Glide.with(context).load(R.mipmap.def_head_boy).into(civCheckHomeWorkTeacherIcon)
        }
        // 姓名
        helper.getView<TextView>(R.id.tvTeacherCommentName).text = item.teacherName + "老师"
        // 内容
        helper.getView<TextView>(R.id.tvTeacherCommentContent).text = item.content
        // 日期
        helper.getView<TextView>(R.id.tvTeacherCommentTime).text = item.commentDate
    }
}