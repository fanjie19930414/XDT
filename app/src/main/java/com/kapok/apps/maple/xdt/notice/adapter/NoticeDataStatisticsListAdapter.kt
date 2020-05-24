package com.kapok.apps.maple.xdt.notice.adapter

import android.graphics.Color
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsStudentBean
import com.kotlin.baselibrary.utils.GlideUtils
import kotlin.math.exp

/**
 * 通知数据统计Adapter
 */
class NoticeDataStatisticsListAdapter(dataList: MutableList<MultiItemEntity>) :
    BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(dataList) {
    private lateinit var adapterClickListener: AdapterClickListener

    companion object {
        const val TYPE_LEVEL_0 = 0
        const val TYPE_LEVEL_1 = 1
        // 判断是 未回复/已回复 选项
        var receiptState = 1
        var classId = 0
    }

    fun setSubmitState(state: Int) {
        receiptState = state
    }

    interface AdapterClickListener {
        fun onAdapterClick(bean: NoticeDataStatisticsStudentBean)
    }

    fun setOnAdapterClickListener(listener: AdapterClickListener) {
        adapterClickListener = listener
    }

    init {
        addItemType(TYPE_LEVEL_0, R.layout.item_commit_homework_class_info)
        addItemType(TYPE_LEVEL_1, R.layout.item_commit_homework_student_info)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        when(helper?.itemViewType) {
            TYPE_LEVEL_0 -> {
                val noticeDataBean = item as NoticeDataStatisticsBean
                // 班级名称
                val className = noticeDataBean.startYear.toString() + "级" + noticeDataBean.grade + noticeDataBean.className + "班" + "(" + noticeDataBean.studentDetails?.size + "人)"
                helper.setText(R.id.tvCommitClassInfo, className)
                    .setImageResource(
                        R.id.ivCommitClassInfo,
                        if (noticeDataBean.isExpanded) R.mipmap.prev_list_d else R.mipmap.prev_list_r
                    )

                // Item伸缩事件
                helper.itemView.setOnClickListener {
                    val position = helper.adapterPosition
                    if (noticeDataBean.isExpanded) {
                        collapse(position, false)
                    } else {
                        expand(position, false)
                    }
                }
            }
            TYPE_LEVEL_1 -> {
                val studentInfoBean = item as NoticeDataStatisticsStudentBean
                // 头像
                if (studentInfoBean.studentAvatar != null && studentInfoBean.studentAvatar.isNotEmpty()) {
                    GlideUtils.loadImage(mContext, studentInfoBean.studentAvatar, helper.getView(R.id.civCommitHomeWorkStudentIcon))
                } else {
                    Glide.with(mContext).load(R.mipmap.def_head_boy).into(helper.getView(R.id.civCommitHomeWorkStudentIcon))
                }
                // 姓名
                helper.setText(R.id.tvCommitStudentInfo, studentInfoBean.studentName)
                // 标签 1未回复 2已回复
                if (receiptState == 1) {
                    // 未回复
                    if (studentInfoBean.commentStatus == 0) {
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setBackgroundResource(R.drawable.shape_commit_to_remind)
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setTextColor(Color.parseColor("#5ABE1E"))
                        helper.getView<TextView>(R.id.tvCommitStudentTag).text = "提醒TA"
                    } else {
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setBackgroundResource(R.drawable.shape_commit_have_assess)
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setTextColor(Color.parseColor("#A1A8B2"))
                        helper.getView<TextView>(R.id.tvCommitStudentTag).text = "已提醒"
                    }
                }
                // 子Item点击事件
                helper.itemView.setOnClickListener {
                    when(receiptState) {
                        1 -> {
                            if (studentInfoBean.commentStatus == 0) {
                                adapterClickListener.onAdapterClick(studentInfoBean)
                            }
                        }
                        2 -> {
                            adapterClickListener.onAdapterClick(studentInfoBean)
                        }
                    }
                }
            }
        }
    }
}