package com.kapok.apps.maple.xdt.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.MyChildrenBean
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.GlideUtils
import de.hdodenhof.circleimageview.CircleImageView

/**
 *  我的孩子Adapter
 *  fanjie
 */
@SuppressLint("SetTextI18n")
class MyChildrenAdapter(private val context: Context, dataList: MutableList<MyChildrenBean>) :
    BaseQuickAdapter<MyChildrenBean, BaseViewHolder>(R.layout.item_my_children, dataList) {

    override fun convert(helper: BaseViewHolder, item: MyChildrenBean?) {
        // 已加入
        val rlChildrenHaveJoined = helper.getView<RelativeLayout>(R.id.rlChildrenHaveJoined)
        val ivChildrenHaveJoined = helper.getView<CircleImageView>(R.id.ivChildrenHaveJoined)
        val tvChildrenHaveJoined = helper.getView<TextView>(R.id.tvChildrenHaveJoined)
        val tvChildrenHaveJoinedDesc = helper.getView<TextView>(R.id.tvChildrenHaveJoinedDesc)
        val tvJoinedLeftHomeWork = helper.getView<TextView>(R.id.tvJoinedLeftHomeWork)
        val tvJoinedLeftNotice = helper.getView<TextView>(R.id.tvJoinedLeftNotice)
        helper.addOnClickListener(R.id.llLeftHomeWork)
        helper.addOnClickListener(R.id.llLeftNotice)
        // 未加入
        val rlChildrenNoJoined = helper.getView<RelativeLayout>(R.id.rlChildrenNoJoined)
        val ivChildrenNoJoined = helper.getView<CircleImageView>(R.id.ivChildrenNoJoined)
        val tvChildrenNoJoined = helper.getView<TextView>(R.id.tvChildrenNoJoined)
        helper.addOnClickListener(R.id.tvChildrenGoToJoined)
        // 已申请
        val rlChildrenApply = helper.getView<RelativeLayout>(R.id.rlChildrenApply)
        val ivChildrenApply = helper.getView<CircleImageView>(R.id.ivChildrenApply)
        val tvChildrenApply = helper.getView<TextView>(R.id.tvChildrenApply)
        val tvChildrenApplyContent = helper.getView<TextView>(R.id.tvChildrenApplyContent)

        if (item?.classState != null) {
            // 0 审核中   1 已通过
            if (item.classState == 0) {
                rlChildrenHaveJoined.setVisible(false)
                rlChildrenNoJoined.setVisible(false)
                rlChildrenApply.setVisible(true)
                // 头像
                if (item.avatar != null && item.avatar.isNotEmpty()) {
                    GlideUtils.loadImage(context, item.avatar, ivChildrenApply)
                } else {
                    if ("famale" == item.sex) {
                        Glide.with(context).load(R.mipmap.def_head_girl).into(ivChildrenApply)
                    } else {
                        Glide.with(context).load(R.mipmap.def_head_boy).into(ivChildrenApply)
                    }
                }
                // 姓名
                if (item.realName != null && item.realName.isNotEmpty()) {
                    tvChildrenApply.text = item.realName
                }
                // 班级名称
                if (item.className != null && item.className.isNotEmpty()) {
                    tvChildrenApplyContent.text = item.className + "班"
                }
            } else if (item.classState == 1) {
                rlChildrenHaveJoined.setVisible(true)
                rlChildrenNoJoined.setVisible(false)
                rlChildrenApply.setVisible(false)
                // 头像
                if (item.avatar != null && item.avatar.isNotEmpty()) {
                    GlideUtils.loadImage(context, item.avatar, ivChildrenHaveJoined)
                } else {
                    if ("famale" == item.sex) {
                        Glide.with(context).load(R.mipmap.def_head_girl).into(ivChildrenHaveJoined)
                    } else {
                        Glide.with(context).load(R.mipmap.def_head_boy).into(ivChildrenHaveJoined)
                    }
                }
                // 姓名
                if (item.realName != null && item.realName.isNotEmpty()) {
                    tvChildrenHaveJoined.text = item.realName
                }
                // 班级名称
                if (item.className != null && item.className.isNotEmpty()) {
                    tvChildrenHaveJoinedDesc.text = item.className + "班"
                }
                // 剩余作业
                if (item.leftWorks != null) {
                    tvJoinedLeftHomeWork.text = item.leftWorks.toString()
                } else {
                    tvJoinedLeftHomeWork.text = "0"
                }
                // 未读通知
                if (item.unReadMessage != null) {
                    tvJoinedLeftNotice.text = item.unReadMessage.toString()
                } else {
                    tvJoinedLeftNotice.text = "0"
                }
            } else {
                // 未加入班级
                rlChildrenHaveJoined.setVisible(false)
                rlChildrenNoJoined.setVisible(true)
                rlChildrenApply.setVisible(false)
                // 头像
                if (item.avatar != null && item.avatar.isNotEmpty()) {
                    GlideUtils.loadImage(context, item.avatar, ivChildrenNoJoined)
                } else {
                    if ("famale" == item.sex) {
                        Glide.with(context).load(R.mipmap.def_head_girl).into(ivChildrenNoJoined)
                    } else {
                        Glide.with(context).load(R.mipmap.def_head_boy).into(ivChildrenNoJoined)
                    }
                }
                // 姓名
                if (item.realName != null && item.realName.isNotEmpty()) {
                    tvChildrenNoJoined.text = item.realName
                }

            }
        } else {
            // 未加入班级
            rlChildrenHaveJoined.setVisible(false)
            rlChildrenNoJoined.setVisible(true)
            rlChildrenApply.setVisible(false)
            if (item != null) {
                // 头像
                if (item.avatar != null && item.avatar.isNotEmpty()) {
                    GlideUtils.loadImage(context, item.avatar, ivChildrenNoJoined)
                } else {
                    if ("famale" == item.sex) {
                        Glide.with(context).load(R.mipmap.def_head_girl).into(ivChildrenNoJoined)
                    } else {
                        Glide.with(context).load(R.mipmap.def_head_boy).into(ivChildrenNoJoined)
                    }
                }
                // 姓名
                if (item.realName != null && item.realName.isNotEmpty()) {
                    tvChildrenNoJoined.text = item.realName
                }
            }

        }

    }
}