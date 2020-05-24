package com.kapok.apps.maple.xdt.addressbook.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookHaveHandel
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookNoHandel
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.GlideUtils

/**
 * 申请列表Adapter
 */
class AddressBookNewPersonHandleAdapter(
    val context: Context,
    dataList: MutableList<AddressBookHaveHandel>
) : BaseQuickAdapter<AddressBookHaveHandel, BaseViewHolder>(R.layout.item_apply_newperson, dataList) {

    override fun convert(helper: BaseViewHolder, item: AddressBookHaveHandel?) {
        val civNewPersonIcon = helper.getView<ImageView>(R.id.civNewPersonIcon)
        val tvApplyName = helper.getView<TextView>(R.id.tvApplyName)
        val tvApplySubject = helper.getView<TextView>(R.id.tvApplySubject)
        val tvStudentApply = helper.getView<TextView>(R.id.tvStudentApply)
        val tvTeacherApply = helper.getView<TextView>(R.id.tvTeacherApply)
        val tvAgreeApply = helper.getView<TextView>(R.id.tvAgreeApply)
        val tvRefuseApply = helper.getView<TextView>(R.id.tvRefuseApply)
        tvAgreeApply.setVisible(false)
        tvRefuseApply.setVisible(true)
        tvRefuseApply.text = item?.stateDesc
        // 头像
        if (item != null) {
            if (item.avatar != null && item.avatar.isNotEmpty()) {
                GlideUtils.loadImage(context, item.avatar, civNewPersonIcon)
            } else {
                Glide.with(context).load(R.mipmap.def_head_boy).into(civNewPersonIcon)
            }
        }
        // 名字
        tvApplyName.text = item?.realName
        // 学科
        if (item?.subjectName != null) {
            tvApplySubject.setVisible(true)
            tvApplySubject.text = item.subjectName
        } else {
            tvApplySubject.setVisible(false)
        }
        // 身份 0学生 2老师
        if (item?.identityType == 0) {
            tvStudentApply.setVisible(true)
            tvTeacherApply.setVisible(false)
        } else {
            tvStudentApply.setVisible(false)
            tvTeacherApply.setVisible(true)
        }
    }
}