package com.kapok.apps.maple.xdt.classlist.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.GlideUtils

/**
 *  家长班级列表Adapter
 *  fanjie
 */
class ClassListParentAdapter(
    dataList: MutableList<ParentClassListBean>,
    private var isTeacher: Boolean
) : BaseQuickAdapter<ParentClassListBean, BaseViewHolder>(R.layout.item_class_list_parent, dataList) {

    override fun convert(helper: BaseViewHolder, item: ParentClassListBean?) {
        val ivClassPic = helper.getView<ImageView>(R.id.ivClassPic)
        val tvClassName = helper.getView<TextView>(R.id.tvClassName)
        val tvClassChildName = helper.getView<TextView>(R.id.tvClassChildName)
        val tvClassTeacherName = helper.getView<TextView>(R.id.tvClassTeacherName)
        val tvContact = helper.getView<TextView>(R.id.tvContact)
        // 加入班级
        val llHaveJoinedClass = helper.getView<LinearLayout>(R.id.llHaveJoinedClass)
        // 未加入班级
        val llNoJoinedClass = helper.getView<LinearLayout>(R.id.llNoJoinedClass)

        // adapter 点击事件
        helper.addOnClickListener(R.id.rlClassDetail)
        helper.addOnClickListener(R.id.llClassNotice)
        helper.addOnClickListener(R.id.llClassContactTeacher)
        helper.addOnClickListener(R.id.tvCancelJoin)
        // 班级头像
        if (item?.avatar != null && item.avatar.isNotEmpty()) {
            Glide.with(mContext).load(item.avatar).into(ivClassPic)
        } else {
            Glide.with(mContext).load(R.mipmap.def_head_class).into(ivClassPic)
        }
        // 班级名称
        tvClassName.text = item?.grade + item?.className + "班"
        // 孩子名称
        if (isTeacher) {
            tvClassChildName.setVisible(false)
            tvContact.text = "联系家长"
        } else {
            tvClassChildName.setVisible(true)
            tvClassChildName.text = item?.studentName
            tvContact.text = "联系老师"
        }
        // 班主任名称
        tvClassTeacherName.text = "班主任：" + item?.headerTeacher
        // 判断是否申请加入了班级 0:审核中（待审核） 1：已通过(已加入)
        if (item?.state == 1) {
            llHaveJoinedClass.setVisible(true)
            llNoJoinedClass.setVisible(false)
        } else {
            llHaveJoinedClass.setVisible(false)
            llNoJoinedClass.setVisible(true)
        }
    }
}