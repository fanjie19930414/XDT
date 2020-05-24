package com.kapok.apps.maple.xdt.addressbook.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildParentBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookNoHandel
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookStudentDetails
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetails
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.GlideUtils

/**
 * 通讯录列表Adapter(学生家长)
 */
@SuppressLint("SetTextI18n")
class AddressBookStudentParentListAdapter(
    val context: Context,
    dataList: MutableList<AddressBookChildParentBean>
) : BaseQuickAdapter<AddressBookChildParentBean, BaseViewHolder>(R.layout.addressbook_list_item_parent_tele, dataList) {

    override fun convert(helper: BaseViewHolder, item: AddressBookChildParentBean?) {
        val civIcon = helper.getView<ImageView>(R.id.civTeacherIcon)
        val name = helper.getView<TextView>(R.id.tvAddressBookName)
        // 头像
        if (item != null) {
            if (item.avatar != null && item.avatar.isNotEmpty()) {
                GlideUtils.loadImage(context, item.avatar, civIcon)
            } else {
                Glide.with(context).load(R.mipmap.def_head_boy).into(civIcon)
            }
        }
        // 姓名
        name.text = item?.realName + "(" + item?.relation + ")"
        // 电话
        helper.addOnClickListener(R.id.ivAddressBookParentDetail)
    }
}