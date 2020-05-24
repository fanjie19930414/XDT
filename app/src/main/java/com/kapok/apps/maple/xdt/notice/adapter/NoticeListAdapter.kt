package com.kapok.apps.maple.xdt.notice.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListItemBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeListItemBean
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.GlideUtils
import de.hdodenhof.circleimageview.CircleImageView

/**
 *  通知列表Adapter
 */
class NoticeListAdapter(
    private val context: Context,
    dataList: MutableList<NoticeListItemBean>,
    private val isTeacher: Boolean
) :
    BaseQuickAdapter<NoticeListItemBean, BaseViewHolder>(
        R.layout.item_homework_list_teacher,
        dataList
    ) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: NoticeListItemBean) {
        val civHomeWorkTeacher = helper.getView<CircleImageView>(R.id.civHomeWorkTeacher)
        val tvHomeWorkName = helper.getView<TextView>(R.id.tvHomeWorkName)
        val tvHomeWorkDesc = helper.getView<TextView>(R.id.tvHomeWorkDesc)
        val tvHomeWorkTitle = helper.getView<TextView>(R.id.tvHomeWorkTitle)
        val tvHomeWorkContent = helper.getView<TextView>(R.id.tvHomeWorkContent)
        val llHomeWorkPic = helper.getView<LinearLayout>(R.id.llHomeWorkPic)
        val ivHomeWorkPic1 = helper.getView<ImageView>(R.id.ivHomeWorkPic1)
        val ivHomeWorkPic2 = helper.getView<ImageView>(R.id.ivHomeWorkPic2)
        val ivHomeWorkPic3 = helper.getView<ImageView>(R.id.ivHomeWorkPic3)
        val tvHomeWorkTime = helper.getView<TextView>(R.id.tvHomeWorkTime)
        val tvHomeWorkNum = helper.getView<TextView>(R.id.tvHomeWorkNum)
        val ivHomeWorkIcon = helper.getView<ImageView>(R.id.ivHomeWorkIcon)
        val ivHomeWorkParentTag = helper.getView<ImageView>(R.id.ivHomeWorkParentTag)
        val ivNoticeLabel = helper.getView<ImageView>(R.id.ivNoticeLabel)
        ivNoticeLabel.setImageResource(R.mipmap.label_notice)
        // 判断是否是老师
        if (isTeacher) {
            ivHomeWorkParentTag.setVisible(false)
        } else {
            ivHomeWorkParentTag.setVisible(true)
        }
        // 头像
        if (item.teacherAvatar != null && item.teacherAvatar.isNotEmpty()) {
            GlideUtils.loadImage(context, item.teacherAvatar, civHomeWorkTeacher)
        } else {
            Glide.with(context).load(R.mipmap.def_head_boy).into(civHomeWorkTeacher)
        }
        // 姓名 / 头衔
        tvHomeWorkName.text = item.teacherName
        if (item.isTeacherLeader) {
            tvHomeWorkDesc.text = "（班主任）"
        } else {
//            tvHomeWorkDesc.text = "（" + item.subjectName + "老师）"
        }
        // 标题
        tvHomeWorkTitle.text = item.title
        // 内容
        tvHomeWorkContent.text = item.content
        // 图片
        if (item.images.isEmpty()) {
            llHomeWorkPic.setVisible(false)
        } else {
            llHomeWorkPic.setVisible(true)
            val images = item.images.split(",")
            when (images.size) {
                1 -> {
                    GlideUtils.loadImage(context, images[0], ivHomeWorkPic1)
                    ivHomeWorkPic2.visibility = View.INVISIBLE
                    ivHomeWorkPic3.visibility = View.INVISIBLE
                }
                2 -> {
                    ivHomeWorkPic2.visibility = View.VISIBLE
                    ivHomeWorkPic3.visibility = View.INVISIBLE
                    GlideUtils.loadImage(context, images[0], ivHomeWorkPic1)
                    GlideUtils.loadImage(context, images[1], ivHomeWorkPic2)
                }
                else -> {
                    ivHomeWorkPic2.visibility = View.VISIBLE
                    ivHomeWorkPic3.visibility = View.VISIBLE
                    GlideUtils.loadImage(context, images[0], ivHomeWorkPic1)
                    GlideUtils.loadImage(context, images[1], ivHomeWorkPic2)
                    GlideUtils.loadImage(context, images[2], ivHomeWorkPic3)
                }
            }
        }
        // 时间
        val year = item.gmtCreate.substring(0, 4)
        var time = item.gmtCreate.substring(0,item.gmtCreate.length - 3)
        if (DateUtils.getYear().toString() == year) {
            time = time.substring(5)
            tvHomeWorkTime.text = time
        } else {
            tvHomeWorkTime.text = time
        }

        // 状态
        if (item.state == 1) {
            tvHomeWorkNum.setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
            tvHomeWorkNum.text = "进行中"
            ivHomeWorkIcon.setImageResource(R.mipmap.class_prev)
        } else {
            tvHomeWorkNum.setTextColor(context.resources.getColor(R.color.text_xdt_hint))
            tvHomeWorkNum.text = "已结束"
            ivHomeWorkIcon.setImageResource(R.mipmap.prev_list_r)
        }

        // 家长 提交状态Tag
        when {
            item.submitStatus == 1 -> {
                ivHomeWorkParentTag.setImageResource(R.mipmap.label_unread)
            }
            item.submitStatus == 2 -> {
                if (item.receiptStatus == 2) {
                    ivHomeWorkParentTag.setImageResource(R.mipmap.label_read)
                } else {
                    ivHomeWorkParentTag.setImageResource(R.mipmap.label_unread)
                }

            }
        }
    }
}