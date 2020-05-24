package com.kapok.apps.maple.xdt.usercenter.adapter

import android.content.Context
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.bean.JoinClassBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_class_detail_teacher.*

/**
 *  加入班级Adapter
 *  fanjie
 */
class JoinClassAdapter(private val context: Context, dataList: ArrayList<JoinClassBean>) :
    BaseQuickAdapter<JoinClassBean, BaseViewHolder>(R.layout.item_join_class, dataList) {

    override fun convert(helper: BaseViewHolder, item: JoinClassBean) {
        val civIconClassName = helper.getView<CustomRoundAngleImageView>(R.id.civIconClassName)
        // 头像
        if (item.avatar != null && item.avatar.isNotEmpty()) {
            GlideUtils.loadImage(context, item.avatar, civIconClassName)
        } else {
            Glide.with(context).load(R.mipmap.def_head_class).into(civIconClassName)
        }
        helper.getView<TextView>(R.id.tvGradeClass).text = item?.grade + item?.className
        helper.getView<TextView>(R.id.tvClassTeacherName).text = item?.headerTeacher
        helper.getView<TextView>(R.id.tvClassNumber).text = item?.startYear.toString() + "级·班级号：" + item?.classId
        helper.addOnClickListener(R.id.tvJoinClass)
    }
}