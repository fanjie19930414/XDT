package com.kapok.apps.maple.xdt.addressbook.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookNoHandel
import com.kotlin.baselibrary.utils.GlideUtils

/**
 * 申请列表头像Adapter
 */
class AddressBookTeacherApplyAdapter(
    val context: Context,
    dataList: MutableList<AddressBookNoHandel>
) : BaseQuickAdapter<AddressBookNoHandel, BaseViewHolder>(R.layout.apply_pic_item, dataList) {

    override fun convert(helper: BaseViewHolder, item: AddressBookNoHandel?) {
        val civIcon = helper.getView<ImageView>(R.id.civApplyIcon)
        // 头像
        if (item != null) {
            if (item.avatar != null && item.avatar.isNotEmpty()) {
                GlideUtils.loadImage(context, item.avatar, civIcon)
            } else {
                Glide.with(context).load(R.mipmap.def_head_boy).into(civIcon)
            }
        }
    }
}