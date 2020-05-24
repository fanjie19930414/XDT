package com.kapok.apps.maple.xdt.timetable.adapter

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean.TimeTableSubjectBean
import com.kotlin.baselibrary.ex.setVisible

/**
 * 课程表展示Adapter（老师）
 */
class SubjectTeacherAdapter(context: Context, dataList: MutableList<TimeTableSubjectBean>, isEdited: Boolean) :
    BaseQuickAdapter<TimeTableSubjectBean, BaseViewHolder>(R.layout.item_morning_class, dataList) {

    private lateinit var onSubjectItemClickListener: OnSubjectItemClickListener
    // 是否是编辑模式
    private var isEdit = isEdited

    fun setIsEdit(isEdited: Boolean) {
        this.isEdit = isEdited
    }

    interface OnSubjectItemClickListener {
        // 周几   第几节课
        fun onSubjectItemClick(weekend: Int, subjectNum: Int)
    }

    fun setOnSubjectItemClickListener(listener: OnSubjectItemClickListener) {
        this.onSubjectItemClickListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: TimeTableSubjectBean) {
        if (isEdit) {
            helper.getView<TextView>(R.id.tvSubjectNum_morning).text = (helper.layoutPosition + 1).toString()
            // 编辑模式 显示老师姓名
            helper.getView<TextView>(R.id.tvTeacher1_morning).setVisible(true)
            helper.getView<TextView>(R.id.tvTeacher2_morning).setVisible(true)
            helper.getView<TextView>(R.id.tvTeacher3_morning).setVisible(true)
            helper.getView<TextView>(R.id.tvTeacher4_morning).setVisible(true)
            helper.getView<TextView>(R.id.tvTeacher5_morning).setVisible(true)
            helper.getView<TextView>(R.id.tvTeacher6_morning).setVisible(true)
            helper.getView<TextView>(R.id.tvTeacher7_morning).setVisible(true)
            // 周一
            if (item.day1 != null) {
                if (item.day1!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject1_morning).text = item.day1!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject1_morning).text = ""
                }
                if (item.day1!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher1_morning).text = item.day1!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher1_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject1_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher1_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject1).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(1, helper.layoutPosition + 1)
            }

            // 周二
            if (item.day2 != null) {
                if (item.day2!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject2_morning).text = item.day2!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject2_morning).text = ""
                }
                if (item.day2!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher2_morning).text = item.day2!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher2_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject2_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher2_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject2).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(2, helper.layoutPosition + 1)
            }

            // 周三
            if (item.day3 != null) {
                if (item.day3!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject3_morning).text = item.day3!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject3_morning).text = ""
                }
                if (item.day3!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher3_morning).text = item.day3!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher3_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject3_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher3_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject3).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(3, helper.layoutPosition + 1)
            }

            //周四
            if (item.day4 != null) {
                if (item.day4!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject4_morning).text = item.day4!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject4_morning).text = ""
                }
                if (item.day4!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher4_morning).text = item.day4!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher4_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject4_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher4_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject4).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(4, helper.layoutPosition + 1)
            }

            // 周五
            if (item.day5 != null) {
                if (item.day5!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject5_morning).text = item.day5!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject5_morning).text = ""
                }
                if (item.day5!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher5_morning).text = item.day5!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher5_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject5_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher5_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject5).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(5, helper.layoutPosition + 1)
            }

            // 周六
            if (item.day6 != null) {
                if (item.day6!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject6_morning).text = item.day6!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject6_morning).text = ""
                }
                if (item.day6!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher6_morning).text = item.day6!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher6_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject6_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher6_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject6).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(6, helper.layoutPosition + 1)
            }

            // 周日
            if (item.day7 != null) {
                if (item.day7!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject7_morning).text = item.day7!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject7_morning).text = ""
                }
                if (item.day7!!.teacherName != null) {
                    helper.getView<TextView>(R.id.tvTeacher7_morning).text = item.day7!!.teacherName
                } else {
                    helper.getView<TextView>(R.id.tvTeacher7_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject7_morning).text = ""
                helper.getView<TextView>(R.id.tvTeacher7_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject7).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(7, helper.layoutPosition + 1)
            }
        } else {
            helper.getView<TextView>(R.id.tvSubjectNum_morning).text = (helper.layoutPosition + 1).toString()
            // 隐藏老师姓名
            helper.getView<TextView>(R.id.tvTeacher1_morning).setVisible(false)
            helper.getView<TextView>(R.id.tvTeacher2_morning).setVisible(false)
            helper.getView<TextView>(R.id.tvTeacher3_morning).setVisible(false)
            helper.getView<TextView>(R.id.tvTeacher4_morning).setVisible(false)
            helper.getView<TextView>(R.id.tvTeacher5_morning).setVisible(false)
            helper.getView<TextView>(R.id.tvTeacher6_morning).setVisible(false)
            helper.getView<TextView>(R.id.tvTeacher7_morning).setVisible(false)
            // 周一
            if (item.day1 != null) {
                if (item.day1!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject1_morning).text = item.day1!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject1_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject1_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject1).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(1, helper.layoutPosition + 1)
            }

            // 周二
            if (item.day2 != null) {
                if (item.day2!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject2_morning).text = item.day2!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject2_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject2_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject2).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(2, helper.layoutPosition + 1)
            }

            // 周三
            if (item.day3 != null) {
                if (item.day3!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject3_morning).text = item.day3!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject3_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject3_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject3).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(3, helper.layoutPosition + 1)
            }

            // 周四
            if (item.day4 != null) {
                if (item.day4!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject4_morning).text = item.day4!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject4_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject4_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject4).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(4, helper.layoutPosition + 1)
            }

            // 周五
            if (item.day5 != null) {
                if (item.day5!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject5_morning).text = item.day5!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject5_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject5_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject5).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(5, helper.layoutPosition + 1)
            }

            // 周六
            if (item.day6 != null) {
                if (item.day6!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject6_morning).text = item.day6!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject6_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject6_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject6).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(6, helper.layoutPosition + 1)
            }

            // 周日
            if (item.day7 != null) {
                if (item.day7!!.subjectName.isNotEmpty()) {
                    helper.getView<TextView>(R.id.tvSubject7_morning).text = item.day7!!.subjectName
                } else {
                    helper.getView<TextView>(R.id.tvSubject7_morning).text = ""
                }
            } else {
                helper.getView<TextView>(R.id.tvSubject7_morning).text = ""
            }
            helper.getView<LinearLayout>(R.id.llSubject7).setOnClickListener {
                onSubjectItemClickListener.onSubjectItemClick(7, helper.layoutPosition + 1)
            }
        }
    }
}