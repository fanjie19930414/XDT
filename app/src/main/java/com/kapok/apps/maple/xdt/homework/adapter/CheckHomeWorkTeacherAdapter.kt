package com.kapok.apps.maple.xdt.homework.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_check_homework_teacher.*

/**
 *  班级列表设置教师端Adapter
 */
@SuppressLint("SetTextI18n")
class CheckHomeWorkTeacherAdapter(private val context: Context, dataList: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_check_homework_teacher, dataList) {

    override fun convert(helper: BaseViewHolder, item: String) {
        val ivCheckHomeWorkImg = helper.getView<CustomRoundAngleImageView>(R.id.ivCheckHomeWorkImg)
        if (item.isNotEmpty()) {
            GlideUtils.loadImage(context, item, ivCheckHomeWorkImg)
        } else {
            Glide.with(context).load(R.mipmap.def_null_child).into(ivCheckHomeWorkImg)
        }
    }
}