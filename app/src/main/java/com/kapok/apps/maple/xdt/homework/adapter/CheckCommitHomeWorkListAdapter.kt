package com.kapok.apps.maple.xdt.homework.adapter

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.CommitHomeWorkClassInfoBean
import com.kapok.apps.maple.xdt.homework.bean.CommitHomeWorkStudentInfoBean
import com.kotlin.baselibrary.utils.GlideUtils

/**
 * 查看提交作业列表Adapter
 */
class CheckCommitHomeWorkListAdapter(dataList: MutableList<MultiItemEntity>) :
    BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(dataList) {
    private lateinit var adapterClickListener: AdapterClickListener

    companion object {
        const val TYPE_LEVEL_0 = 0
        const val TYPE_LEVEL_1 = 1
        // 判断是 未提交/已提交 选项
        var submitState = 1
        var classId = 0
    }

    init {
        addItemType(TYPE_LEVEL_0, R.layout.item_commit_homework_class_info)
        addItemType(TYPE_LEVEL_1, R.layout.item_commit_homework_student_info)
    }

    fun setSubmitState(paramsSubmitState: Int) {
        submitState = paramsSubmitState
    }

    interface AdapterClickListener {
        fun onAdapterClick(bean: CommitHomeWorkStudentInfoBean)
    }

    fun setOnAdapterClickListener(listener: AdapterClickListener) {
        adapterClickListener = listener
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        when (helper?.itemViewType) {
            TYPE_LEVEL_0 -> {
                val classInfoBean = item as CommitHomeWorkClassInfoBean
                // 班级名称
                val className = classInfoBean.startYear.toString() + "级" + classInfoBean.grade + classInfoBean.className + "班" + "(" + classInfoBean.studentDetails.size + "人)"
                helper.setText(R.id.tvCommitClassInfo, className)
                    .setImageResource(
                        R.id.ivCommitClassInfo,
                        if (classInfoBean.isExpanded) R.mipmap.prev_list_d else R.mipmap.prev_list_r
                    )
                helper.itemView.setOnClickListener {
                    val position = helper.adapterPosition
                    if (classInfoBean.isExpanded) {
                        collapse(position, false)
                    } else {
                        expand(position, false)
                    }
                }
            }
            TYPE_LEVEL_1 -> {
                val studentInfoBean = item as CommitHomeWorkStudentInfoBean
                // 头像
                if (studentInfoBean.studentAvatar != null && studentInfoBean.studentAvatar.isNotEmpty()) {
                    GlideUtils.loadImage(mContext, studentInfoBean.studentAvatar, helper.getView(R.id.civCommitHomeWorkStudentIcon))
                } else {
                    Glide.with(mContext).load(R.mipmap.def_head_boy).into(helper.getView(R.id.civCommitHomeWorkStudentIcon))
                }
                // 姓名
                helper.setText(R.id.tvCommitStudentInfo, studentInfoBean.studentName)
                // 标签 1未提交 2已提交
                if (submitState == 1) {
                    // 未提醒
                    if (studentInfoBean.commentStatus == 0) {
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setBackgroundResource(R.drawable.shape_commit_to_remind)
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setTextColor(Color.parseColor("#5ABE1E"))
                        helper.getView<TextView>(R.id.tvCommitStudentTag).text = "提醒TA"
                    } else {
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setBackgroundResource(R.drawable.shape_commit_have_assess)
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setTextColor(Color.parseColor("#A1A8B2"))
                        helper.getView<TextView>(R.id.tvCommitStudentTag).text = "已提醒"
                    }
                } else {
                    // 未点评
                    if (studentInfoBean.commentStatus == 0) {
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setBackgroundResource(R.drawable.shape_commit_to_assess)
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setTextColor(Color.parseColor("#FF6E1E"))
                        helper.getView<TextView>(R.id.tvCommitStudentTag).text = "待点评"
                    } else {
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setBackgroundResource(R.drawable.shape_commit_have_assess)
                        helper.getView<TextView>(R.id.tvCommitStudentTag).setTextColor(Color.parseColor("#A1A8B2"))
                        helper.getView<TextView>(R.id.tvCommitStudentTag).text = "已点评"
                    }
                }
                // 子Item点击事件
                helper.itemView.setOnClickListener {
                    when(submitState) {
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