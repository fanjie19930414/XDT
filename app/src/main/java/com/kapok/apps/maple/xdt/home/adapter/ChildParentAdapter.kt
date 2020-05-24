package com.kapok.apps.maple.xdt.home.adapter

import android.content.Context
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.ChildParentBean
import com.kapok.apps.maple.xdt.usercenter.bean.JoinClassBean
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_children_info.*

/**
 *  孩子信息页家长列表Adapter
 *  fanjie
 */
class ChildParentAdapter(private val context: Context, dataList: MutableList<ChildParentBean>) :
    BaseQuickAdapter<ChildParentBean, BaseViewHolder>(R.layout.item_child_parent, dataList) {

    override fun convert(helper: BaseViewHolder, item: ChildParentBean?) {
        // 姓名
        helper.getView<TextView>(R.id.tvParentRelation).text = item?.userName
        // 头像
        if (item?.avatar != null && item.avatar.isNotEmpty()) {
            GlideUtils.loadImage(context, item.avatar, helper.getView(R.id.civIconParent))
        } else {
            Glide.with(context).load(R.mipmap.def_head_boy).into(helper.getView(R.id.civIconParent))
        }
    }
}