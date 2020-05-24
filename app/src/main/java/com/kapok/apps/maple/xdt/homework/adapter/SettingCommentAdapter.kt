package com.kapok.apps.maple.xdt.homework.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kapok.apps.maple.xdt.homework.bean.TeacherCommentParentBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_check_homework_teacher.*

/**
 *  设置评论Adapter
 */
@SuppressLint("SetTextI18n")
class SettingCommentAdapter(dataList: MutableList<CommonComment>) :
    BaseQuickAdapter<CommonComment, BaseViewHolder>(R.layout.item_setting_comment, dataList) {

    override fun convert(helper: BaseViewHolder, item: CommonComment) {
        helper.getView<TextView>(R.id.tvCommentContent).text = item.content
    }
}