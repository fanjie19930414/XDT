package com.kapok.apps.maple.xdt.classlist.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.utils.Dp2pxUtils

/**
 *  详情IconAdapter
 *  fanjie
 */
class ClassIconAdapter(
    private val context: Context,
    dataList: MutableList<String>,
    private val screenWidth: Int
) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_class_icon, dataList) {

    override fun convert(helper: BaseViewHolder, item: String) {
        val ivDetailIcon = helper.getView<ImageView>(R.id.ivDetailIcon)
        val tvDetailIconName = helper.getView<TextView>(R.id.tvDetailIconName)
        val llDetailIcon = helper.getView<LinearLayout>(R.id.llDetailIcon)
        // 每页展示5个item
        val screenWidth = screenWidth - Dp2pxUtils.dp2px(context, 32)
        val params = llDetailIcon.layoutParams
        params.width = screenWidth / 5
        llDetailIcon.layoutParams = params
        // 数据
        tvDetailIconName.text = item
        when (item) {
            "课程表" -> Glide.with(context).load(R.mipmap.class_schedule).into(ivDetailIcon)
            "作业" -> Glide.with(context).load(R.mipmap.class_homework).into(ivDetailIcon)
            "通知" -> Glide.with(context).load(R.mipmap.class_notice_list).into(ivDetailIcon)
            "通讯录" -> Glide.with(context).load(R.mipmap.class_addr).into(ivDetailIcon)
            "账本" -> Glide.with(context).load(R.mipmap.class_books02).into(ivDetailIcon)
            "邀请" -> Glide.with(context).load(R.mipmap.class_invitation).into(ivDetailIcon)
            "家委会" -> Glide.with(context).load(R.mipmap.class_somepta).into(ivDetailIcon)
        }
    }
}