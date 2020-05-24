package com.kapok.apps.maple.xdt.timetable.activity.timetable_teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.activity.TimeTableSettingActivity
import com.kapok.apps.maple.xdt.timetable.adapter.SelectedSubjectAdapter
import com.kapok.apps.maple.xdt.timetable.adapter.SubjectTeacherAdapter
import com.kapok.apps.maple.xdt.timetable.adapter.TimeTableWeekendAdapter
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableDetailListBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSubjectDetailBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean.TimeTableRowBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean.TimeTableSubjectBean
import com.kapok.apps.maple.xdt.timetable.presenter.TimeTableTeacherPresenter
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableTeacherView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_time_table_teacher.*
import java.util.*

/**
 * 课程表展示（老师）
 */
class TimeTableTeacherActivity : BaseMVPActivity<TimeTableTeacherPresenter>(), TimeTableTeacherView {
    // 课程数据List(上午)
    private var subjectMorningList: MutableList<TimeTableSubjectBean> = arrayListOf()
    private var copySubjectMorningList: MutableList<TimeTableSubjectBean> = arrayListOf()
    // 课程数据List(下午)
    private var subjectNoonList: MutableList<TimeTableSubjectBean> = arrayListOf()
    private var copySubjectNoonList: MutableList<TimeTableSubjectBean> = arrayListOf()
    // 上午课程Adapter
    lateinit var subjectTeacherAdapterMorning: SubjectTeacherAdapter
    // 上午课程数量
    private var morningClassNum: Int = -1
    // 下午课程Adapter
    lateinit var subjectTeacherAdapterNoon: SubjectTeacherAdapter
    // 下午课程数量
    private var noonClassNum: Int = -1
    // 编辑课程存储的集合
    private val timeTableDetailList = arrayListOf<TimeTableSubjectDetailBean>()
    // 副标题
    private lateinit var timeTableName: String
    // 是否显示周数条
    private var isShowWeekend = false
    // 一共多少周
    private var currentWeekNum = -1
    private var totalWeedNum = -1
    // 当前选中的是第几周
    private var selectWeekNum = -1
    private var weekendList: MutableList<Int> = arrayListOf()
    private lateinit var timeTableWeekendAdapter: TimeTableWeekendAdapter
    // 底部弹窗Pop
    lateinit var pop: PopupWindow
    lateinit var parentView: View
    // 更改当前周表示
    private var isCurrentWeek = false
    private var isFutureWeek = false
    private var isAllWeek = false
    // 选择课程数据List
    private lateinit var subjectChooseList: MutableList<ClassChooseSubjectBean>
    // 编辑课程列表Adapter
    private lateinit var selectSubjectTeacherAdapter: SelectedSubjectAdapter
    // 当前选中课程Item
    private var haveChoosedSubject: ClassChooseSubjectBean? = null
    // 是否为辑模式
    private var isEdit: Boolean = false

    // 临时存储day1 - day7 Bean上午
    private var tempDay1Morning: TimeTableRowBean? = null
    private var tempDay2Morning: TimeTableRowBean? = null
    private var tempDay3Morning: TimeTableRowBean? = null
    private var tempDay4Morning: TimeTableRowBean? = null
    private var tempDay5Morning: TimeTableRowBean? = null
    private var tempDay6Morning: TimeTableRowBean? = null
    private var tempDay7Morning: TimeTableRowBean? = null

    // 临时存储day1 - day7 Bean下午
    private var tempDay1Noon: TimeTableRowBean? = null
    private var tempDay2Noon: TimeTableRowBean? = null
    private var tempDay3Noon: TimeTableRowBean? = null
    private var tempDay4Noon: TimeTableRowBean? = null
    private var tempDay5Noon: TimeTableRowBean? = null
    private var tempDay6Noon: TimeTableRowBean? = null
    private var tempDay7Noon: TimeTableRowBean? = null

    private var classId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table_teacher)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = TimeTableTeacherPresenter(this)
        mPresenter.mView = this
        // 页面初始化配置
        initData()
        initPop()
    }

    private fun initData() {
        classId = intent.getIntExtra("classId", -1)
        // 获取当前月份
        val currentMonth = DateUtils.getMonth()
        tvWeedendMonthTeacher.text = currentMonth.toString()
        // 显示有多少周的列表
        timeTableWeekendAdapter = TimeTableWeekendAdapter(this, weekendList)
        rvTimeTableWeekend.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTimeTableWeekend.adapter = timeTableWeekendAdapter

        // 上午列表
        subjectTeacherAdapterMorning = SubjectTeacherAdapter(this, subjectMorningList, isEdit)
        rvMorningTeacher.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMorningTeacher.adapter = subjectTeacherAdapterMorning

        // 下午列表
        subjectTeacherAdapterNoon = SubjectTeacherAdapter(this, subjectNoonList, isEdit)
        rvNoonTeacher.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvNoonTeacher.adapter = subjectTeacherAdapterNoon

        // 选中课程的列表
        subjectChooseList = arrayListOf()
        selectSubjectTeacherAdapter = SelectedSubjectAdapter(this, subjectChooseList)
        rvSelected.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSelected.adapter = selectSubjectTeacherAdapter

        // 获取课程表设置接口
        mPresenter.getSubjectSetting(classId)
        // 获取班级科目列表
        mPresenter.getClassSubjectList(classId)
    }

    private fun initPop() {
        pop = PopupWindow(this)
        parentView = LayoutInflater.from(this).inflate(R.layout.layout_spinner_list, null)
        pop.contentView = parentView
        pop.height = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.width = ViewGroup.LayoutParams.WRAP_CONTENT
        parentView.findViewById<TextView>(R.id.tvChangeWeek).setOnClickListener {
            pop.dismiss()
        }
        pop.isTouchable = true
        pop.isFocusable = true
        pop.setBackgroundDrawable(resources.getDrawable(R.color.transparent))
        pop.isOutsideTouchable = true
        pop.update()
        pop.setOnDismissListener {
            val lp = window.attributes
            lp.alpha = 1f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = lp
        }
    }

    private fun initListener() {
        // 编辑取消按钮
        mLeftIvEdit.setOnClickListener {
            isEdit = false
            subjectTeacherAdapterMorning.setIsEdit(isEdit)
            subjectTeacherAdapterMorning.setNewData(copySubjectMorningList)
            subjectTeacherAdapterNoon.setIsEdit(isEdit)
            subjectTeacherAdapterNoon.setNewData(copySubjectNoonList)
            FAB.setVisible(true)
            rlTimeTable.setVisible(true)
            rlEditTimeTable.setVisible(false)
            rvSelected.setVisible(false)
        }
        // 编辑确认按钮
        ivTimeTableSettingEdit.setOnClickListener {
            timeTableDetailList.clear()
            for (index in subjectMorningList.indices) {
                if (subjectMorningList[index].day1 != null) {
                    val subjectId = subjectMorningList[index].day1?.subjectId
                    val subjectName = subjectMorningList[index].day1?.subjectName
                    val lessonNumber = subjectMorningList[index].day1?.lessonNumber
                    val lessonType = subjectMorningList[index].day1?.lessonType
                    val weekDay = subjectMorningList[index].day1?.weekDay
                    val teacherId = subjectMorningList[index].day1?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectMorningList[index].day2 != null) {
                    val subjectId = subjectMorningList[index].day2?.subjectId
                    val subjectName = subjectMorningList[index].day2?.subjectName
                    val lessonNumber = subjectMorningList[index].day2?.lessonNumber
                    val lessonType = subjectMorningList[index].day2?.lessonType
                    val weekDay = subjectMorningList[index].day2?.weekDay
                    val teacherId = subjectMorningList[index].day2?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectMorningList[index].day3 != null) {
                    val subjectId = subjectMorningList[index].day3?.subjectId
                    val subjectName = subjectMorningList[index].day3?.subjectName
                    val lessonNumber = subjectMorningList[index].day3?.lessonNumber
                    val lessonType = subjectMorningList[index].day3?.lessonType
                    val weekDay = subjectMorningList[index].day3?.weekDay
                    val teacherId = subjectMorningList[index].day3?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectMorningList[index].day4 != null) {
                    val subjectId = subjectMorningList[index].day4?.subjectId
                    val subjectName = subjectMorningList[index].day4?.subjectName
                    val lessonNumber = subjectMorningList[index].day4?.lessonNumber
                    val lessonType = subjectMorningList[index].day4?.lessonType
                    val weekDay = subjectMorningList[index].day4?.weekDay
                    val teacherId = subjectMorningList[index].day4?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectMorningList[index].day5 != null) {
                    val subjectId = subjectMorningList[index].day5?.subjectId
                    val subjectName = subjectMorningList[index].day5?.subjectName
                    val lessonNumber = subjectMorningList[index].day5?.lessonNumber
                    val lessonType = subjectMorningList[index].day5?.lessonType
                    val weekDay = subjectMorningList[index].day5?.weekDay
                    val teacherId = subjectMorningList[index].day5?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectMorningList[index].day6 != null) {
                    val subjectId = subjectMorningList[index].day6?.subjectId
                    val subjectName = subjectMorningList[index].day6?.subjectName
                    val lessonNumber = subjectMorningList[index].day6?.lessonNumber
                    val lessonType = subjectMorningList[index].day6?.lessonType
                    val weekDay = subjectMorningList[index].day6?.weekDay
                    val teacherId = subjectMorningList[index].day6?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectMorningList[index].day7 != null) {
                    val subjectId = subjectMorningList[index].day7?.subjectId
                    val subjectName = subjectMorningList[index].day7?.subjectName
                    val lessonNumber = subjectMorningList[index].day7?.lessonNumber
                    val lessonType = subjectMorningList[index].day7?.lessonType
                    val weekDay = subjectMorningList[index].day7?.weekDay
                    val teacherId = subjectMorningList[index].day7?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
            }
            // 下午
            for (index in subjectNoonList.indices) {
                if (subjectNoonList[index].day1 != null) {
                    val subjectId = subjectNoonList[index].day1?.subjectId
                    val subjectName = subjectNoonList[index].day1?.subjectName
                    val lessonNumber = subjectNoonList[index].day1?.lessonNumber
                    val lessonType = subjectNoonList[index].day1?.lessonType
                    val weekDay = subjectNoonList[index].day1?.weekDay
                    val teacherId = subjectNoonList[index].day1?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectNoonList[index].day2 != null) {
                    val subjectId = subjectNoonList[index].day2?.subjectId
                    val subjectName = subjectNoonList[index].day2?.subjectName
                    val lessonNumber = subjectNoonList[index].day2?.lessonNumber
                    val lessonType = subjectNoonList[index].day2?.lessonType
                    val weekDay = subjectNoonList[index].day2?.weekDay
                    val teacherId = subjectNoonList[index].day2?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectNoonList[index].day3 != null) {
                    val subjectId = subjectNoonList[index].day3?.subjectId
                    val subjectName = subjectNoonList[index].day3?.subjectName
                    val lessonNumber = subjectNoonList[index].day3?.lessonNumber
                    val lessonType = subjectNoonList[index].day3?.lessonType
                    val weekDay = subjectNoonList[index].day3?.weekDay
                    val teacherId = subjectNoonList[index].day3?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectNoonList[index].day4 != null) {
                    val subjectId = subjectNoonList[index].day4?.subjectId
                    val subjectName = subjectNoonList[index].day4?.subjectName
                    val lessonNumber = subjectNoonList[index].day4?.lessonNumber
                    val lessonType = subjectNoonList[index].day4?.lessonType
                    val weekDay = subjectNoonList[index].day4?.weekDay
                    val teacherId = subjectNoonList[index].day4?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectNoonList[index].day5 != null) {
                    val subjectId = subjectNoonList[index].day5?.subjectId
                    val subjectName = subjectNoonList[index].day5?.subjectName
                    val lessonNumber = subjectNoonList[index].day5?.lessonNumber
                    val lessonType = subjectNoonList[index].day5?.lessonType
                    val weekDay = subjectNoonList[index].day5?.weekDay
                    val teacherId = subjectNoonList[index].day5?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectNoonList[index].day6 != null) {
                    val subjectId = subjectNoonList[index].day6?.subjectId
                    val subjectName = subjectNoonList[index].day6?.subjectName
                    val lessonNumber = subjectNoonList[index].day6?.lessonNumber
                    val lessonType = subjectNoonList[index].day6?.lessonType
                    val weekDay = subjectNoonList[index].day6?.weekDay
                    val teacherId = subjectNoonList[index].day6?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
                if (subjectNoonList[index].day7 != null) {
                    val subjectId = subjectNoonList[index].day7?.subjectId
                    val subjectName = subjectNoonList[index].day7?.subjectName
                    val lessonNumber = subjectNoonList[index].day7?.lessonNumber
                    val lessonType = subjectNoonList[index].day7?.lessonType
                    val weekDay = subjectNoonList[index].day7?.weekDay
                    val teacherId = subjectNoonList[index].day7?.teacherId
                    timeTableDetailList.add(
                        TimeTableSubjectDetailBean(
                            lessonNumber,
                            lessonType,
                            subjectId,
                            subjectName,
                            teacherId,
                            weekDay
                        )
                    )
                }
            }
            // 改变当前周
            if (isCurrentWeek) {
                mPresenter.upDateTimeTableDetail(
                    classId.toString(),
                    timeTableDetailList,
                    "$selectWeekNum-$selectWeekNum"
                )
            }
            // 改变当周以后
            if (isFutureWeek) {
                mPresenter.upDateTimeTableDetail(classId.toString(), timeTableDetailList, "$selectWeekNum-0")
            }
            // 改变所有
            if (isAllWeek) {
                mPresenter.upDateTimeTableDetail(classId.toString(), timeTableDetailList, "")
            }
        }
        // 标题点击事件
        llTitle.setOnClickListener {
            if (isShowWeekend) {
                selectWeekNum = currentWeekNum
                mTitleTv.text = "第" + currentWeekNum.toString() + "周"
                rvTimeTableWeekend.scrollToPosition(0)
                mPresenter.getTimeTableInfo(classId, currentWeekNum.toString(), false)
                isShowWeekend = false
                rlTimeTableWeedend.setVisible(false)
            } else {
                mTitleTv.text = "返回当前周"
                isShowWeekend = true
                mPresenter.getTimeTableInfo(classId, currentWeekNum.toString(), false)
                rlTimeTableWeedend.setVisible(true)
            }
        }
        // 有多少周列表点击回调（返回点击的周）
        timeTableWeekendAdapter.setSelectLessonTimeListener(object :
            TimeTableWeekendAdapter.SelectTimeTableWeekendInterface {
            override fun onSelectData(currentWeekend: Int) {
                selectWeekNum = currentWeekend
                mPresenter.getTimeTableInfo(classId, currentWeekend.toString(), true)
            }
        })
        // 返回
        mLeftIv.setOnClickListener { finish() }
        // 设置按钮
        ivTimeTableSetting.setOnClickListener {
            val intent = Intent(this@TimeTableTeacherActivity,TimeTableSettingActivity::class.java)
            intent.putExtra("classId",classId)
            startActivityForResult(intent,100)
        }
        // 底部按钮
        FAB.setOnClickListener { showUp2(it) }
        // 改变当前周
        parentView.findViewById<TextView>(R.id.tvChangeWeek).setOnClickListener {
            isEdit = true
            isCurrentWeek = true
            isFutureWeek = false
            isAllWeek = false
            subjectTeacherAdapterMorning.setIsEdit(isEdit)
            subjectTeacherAdapterMorning.setNewData(subjectMorningList)
            subjectTeacherAdapterNoon.setIsEdit(isEdit)
            subjectTeacherAdapterNoon.setNewData(subjectNoonList)
            // 按钮消失 标题栏改变  选择课程列表弹出
            FAB.setVisible(false)
            rlTimeTable.setVisible(false)
            rlEditTimeTable.setVisible(true)
            rvSelected.setVisible(true)
            isShowWeekend = false
            rlTimeTableWeedend.setVisible(false)
            mTitleTvEdit.text = "第" + selectWeekNum + "周"
            pop.dismiss()
        }
        // 改变当前周以后
        parentView.findViewById<TextView>(R.id.tvChangeFutureWeek).setOnClickListener {
            isEdit = true
            isCurrentWeek = false
            isFutureWeek = true
            isAllWeek = false
            subjectTeacherAdapterMorning.setIsEdit(isEdit)
            subjectTeacherAdapterMorning.setNewData(subjectMorningList)
            subjectTeacherAdapterNoon.setIsEdit(isEdit)
            subjectTeacherAdapterNoon.setNewData(subjectNoonList)
            // 按钮消失 标题栏改变  选择课程列表弹出
            FAB.setVisible(false)
            rlTimeTable.setVisible(false)
            rlEditTimeTable.setVisible(true)
            rvSelected.setVisible(true)
            isShowWeekend = false
            rlTimeTableWeedend.setVisible(false)
            mTitleTvEdit.text = "第" + selectWeekNum + "周及以后"
            pop.dismiss()
        }
        // 改变所有周
        parentView.findViewById<TextView>(R.id.tvChangeAllWeek).setOnClickListener {
            isEdit = true
            isCurrentWeek = false
            isFutureWeek = false
            isAllWeek = true
            subjectTeacherAdapterMorning.setIsEdit(isEdit)
            subjectTeacherAdapterMorning.setNewData(subjectMorningList)
            subjectTeacherAdapterNoon.setIsEdit(isEdit)
            subjectTeacherAdapterNoon.setNewData(subjectNoonList)
            // 按钮消失 标题栏改变  选择课程列表弹出
            FAB.setVisible(false)
            rlTimeTable.setVisible(false)
            rlEditTimeTable.setVisible(true)
            rvSelected.setVisible(true)
            isShowWeekend = false
            rlTimeTableWeedend.setVisible(false)
            mTitleTvEdit.text = "第" + 1 + "周 ~ 第" + totalWeedNum + "周"
            pop.dismiss()
        }
        // 选中列表的回调
        selectSubjectTeacherAdapter.setSelectLessonTimeListener(object : SelectedSubjectAdapter.SelectItemInterface {
            override fun onSelectData(subject: ClassChooseSubjectBean?) {
                haveChoosedSubject = subject
            }
        })
        // 课程表主列表(上午)点击回调
        subjectTeacherAdapterMorning.setOnSubjectItemClickListener(object :
            SubjectTeacherAdapter.OnSubjectItemClickListener {
            override fun onSubjectItemClick(weekend: Int, subjectNum: Int) {
                // 第几节课的bean
                val bean = subjectMorningList[subjectNum - 1]
                // 存储老师姓名和id
                val teacherId: String?
                val teacherName: String?
                when (weekend) {
                    1 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day1 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day1 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 1, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                    2 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day2 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day2 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 2, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                    3 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day3 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day3 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 3, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                    4 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day4 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day4 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 4, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                    5 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day5 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day5 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 5, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                    6 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day6 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day6 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 6, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                    7 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day7 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day7 = TimeTableRowBean(
                                    subjectNum, 1, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 7, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterMorning.notifyDataSetChanged()
                    }
                }
            }
        })
        // 课程表主列表(下午)点击回调
        subjectTeacherAdapterNoon.setOnSubjectItemClickListener(object :
            SubjectTeacherAdapter.OnSubjectItemClickListener {
            override fun onSubjectItemClick(weekend: Int, subjectNum: Int) {
                // 第几节课的bean
                val bean = subjectNoonList[subjectNum - 1]
                // 存储老师姓名和id
                val teacherId: String
                val teacherName: String
                when (weekend) {
                    1 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day1 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day1 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 1, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                    2 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day2 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day2 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 2, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                    3 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day3 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day3 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 3, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                    4 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day4 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day4 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 4, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                    5 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day5 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day5 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 5, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                    6 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day6 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day6 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 6, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                    7 -> {
                        if (isEdit) {
                            if (haveChoosedSubject == null) {
                                bean.day7 = null
                            } else {
                                if (haveChoosedSubject!!.teacherOutPutVOList.size > 0) {
                                    teacherId = (haveChoosedSubject!!.teacherOutPutVOList[0].teacherId).toString()
                                    teacherName = haveChoosedSubject!!.teacherOutPutVOList[0].teacherName
                                } else {
                                    teacherId = ""
                                    teacherName = ""
                                }
                                bean.day7 = TimeTableRowBean(
                                    subjectNum, 2, haveChoosedSubject!!.subjectId, haveChoosedSubject!!.subjectName,
                                    teacherId, 7, teacherName
                                )
                            }
                        }
                        subjectTeacherAdapterNoon.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    // 设置显示在v上方（以v的中心位置为开始位置）
    private fun showUp2(v: View) {
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = parentView.measuredHeight
        val popupWidth = parentView.measuredWidth

        // 产生背景变暗效果
        val lp = window.attributes
        lp.alpha = 0.6f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
        //获取需要在其上方显示的控件的位置信息
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        //在控件上方显示
        pop.showAtLocation(
            v,
            Gravity.NO_GRAVITY,
            (location[0] + v.width / 2) - popupWidth / 2 - 100,
            location[1] - popupHeight - 50
        )
    }

    // 获取课程表设置接口
    override fun getSettingInfo(data: TimeTableSettingInfoBean) {
        if (data.timetableConfigDetailList != null && data.timetableConfigDetailList.size > 0) {
            // 上午几节课
            morningClassNum = data.amLessonCount
            // 下午几节课
            noonClassNum = data.pmLessonCount
            // 副标题
            timeTableName = data.timeTableName
            if (timeTableName.isNotEmpty()) {
                mSubTitle.setVisible(true)
                mSubTitle.text = timeTableName
                mSubTitleEdit.text = timeTableName
            } else {
                mSubTitle.setVisible(false)
            }
            // 获取课程表
            mPresenter.getTimeTableInfo(classId, "", false)
        } else {
            ToastUtils.showMsg(this, "您还没有设置课程表")
            val intent = Intent(this@TimeTableTeacherActivity,TimeTableSettingActivity::class.java)
            intent.putExtra("classId",classId)
            startActivityForResult(intent,100)
        }
    }

    // 获取课程表回调
    override fun getTimeTableSubject(data: TimeTableInfoBean) {
        weekendList.clear()
        // 上午数据配置
        subjectMorningList.clear()
        copySubjectMorningList.clear()
        tempDay1Morning = null
        tempDay2Morning = null
        tempDay3Morning = null
        tempDay4Morning = null
        tempDay5Morning = null
        tempDay6Morning = null
        tempDay7Morning = null
        // 下午数据配置
        subjectNoonList.clear()
        copySubjectNoonList.clear()
        tempDay1Noon = null
        tempDay2Noon = null
        tempDay3Noon = null
        tempDay4Noon = null
        tempDay5Noon = null
        tempDay6Noon = null
        tempDay7Noon = null

        for (i in 0 until morningClassNum) {
            subjectMorningList.add(TimeTableSubjectBean())
            copySubjectMorningList.add(TimeTableSubjectBean())
        }
        for (i in 0 until noonClassNum) {
            subjectNoonList.add(TimeTableSubjectBean())
            copySubjectNoonList.add(TimeTableSubjectBean())
        }
        // 显示日期
        val beginData = data.beginDate
        val endData = data.endDate
//        beginData = beginData.split("T")[0]
//        val beginSec = DateUtils.paseDateTomillise(beginData)
//        endData = endData.split("T")[0]
//        val endSec = DateUtils.paseDateTomillise(endData)
        val beginSec = DateUtils.stringToLong(beginData,"yyyy-MM-dd")
        val endSec = DateUtils.stringToLong(endData,"yyyy-MM-dd")
        val timeList = arrayListOf<String>()
        val tempList = arrayListOf<String>()
        for (time in beginSec..endSec step 1000 * 60 * 60 * 24) {
            val weekTime = DateUtils.format(Date(time), "yy-MM-dd")
            tempList.add(weekTime)
            timeList.add(weekTime.substring(3))
        }
        // 获取第一周的课表 第一节课是周几
        val tempWeek = DateUtils.getWeekFromTime(tempList[0])
        // 获取最后一周的课表 判断最后一节课是周几
        val tempLastWeek = DateUtils.getWeekFromTime(tempList[tempList.size - 1])

        // 展示当前周的第一天月份
        tvWeekend1_month_Teacher.text = timeList[0].substring(0,2)

        if (7 <= timeList.size) {
            tvWeekend1_month_Teacher.text = timeList[0]
            tvWeekend2_month_Teacher.text = timeList[1]
            tvWeekend3_month_Teacher.text = timeList[2]
            tvWeekend4_month_Teacher.text = timeList[3]
            tvWeekend5_month_Teacher.text = timeList[4]
            tvWeekend6_month_Teacher.text = timeList[5]
            tvWeekend7_month_Teacher.text = timeList[6]
        } else {
            // 第一周
            if (1 == data.weekNumber) {
                when (tempWeek) {
                    2 -> {
                        tvWeekend1_month_Teacher.text = ""
                        tvWeekend2_month_Teacher.text = timeList[0]
                        tvWeekend3_month_Teacher.text = timeList[1]
                        tvWeekend4_month_Teacher.text = timeList[2]
                        tvWeekend5_month_Teacher.text = timeList[3]
                        tvWeekend6_month_Teacher.text = timeList[4]
                        tvWeekend7_month_Teacher.text = timeList[5]
                    }
                    3 -> {
                        tvWeekend1_month_Teacher.text = ""
                        tvWeekend2_month_Teacher.text = ""
                        tvWeekend3_month_Teacher.text = timeList[0]
                        tvWeekend4_month_Teacher.text = timeList[1]
                        tvWeekend5_month_Teacher.text = timeList[2]
                        tvWeekend6_month_Teacher.text = timeList[3]
                        tvWeekend7_month_Teacher.text = timeList[4]
                    }
                    4 -> {
                        tvWeekend1_month_Teacher.text = ""
                        tvWeekend2_month_Teacher.text = ""
                        tvWeekend3_month_Teacher.text = ""
                        tvWeekend4_month_Teacher.text = timeList[0]
                        tvWeekend5_month_Teacher.text = timeList[1]
                        tvWeekend6_month_Teacher.text = timeList[2]
                        tvWeekend7_month_Teacher.text = timeList[3]
                    }
                    5 -> {
                        tvWeekend1_month_Teacher.text = ""
                        tvWeekend2_month_Teacher.text = ""
                        tvWeekend3_month_Teacher.text = ""
                        tvWeekend4_month_Teacher.text = ""
                        tvWeekend5_month_Teacher.text = timeList[0]
                        tvWeekend6_month_Teacher.text = timeList[1]
                        tvWeekend7_month_Teacher.text = timeList[2]
                    }
                    6 -> {
                        tvWeekend1_month_Teacher.text = ""
                        tvWeekend2_month_Teacher.text = ""
                        tvWeekend3_month_Teacher.text = ""
                        tvWeekend4_month_Teacher.text = ""
                        tvWeekend5_month_Teacher.text = ""
                        tvWeekend6_month_Teacher.text = timeList[0]
                        tvWeekend7_month_Teacher.text = timeList[1]
                    }
                    7 -> {
                        tvWeekend1_month_Teacher.text = ""
                        tvWeekend2_month_Teacher.text = ""
                        tvWeekend3_month_Teacher.text = ""
                        tvWeekend4_month_Teacher.text = ""
                        tvWeekend5_month_Teacher.text = ""
                        tvWeekend6_month_Teacher.text = ""
                        tvWeekend7_month_Teacher.text = timeList[0]
                    }
                }
                // 最后一周
            } else if (data.weekNumber == data.totalWeekNumber) {
                when (tempLastWeek) {
                    1 -> {
                        tvWeekend1_month_Teacher.text = timeList[0]
                        tvWeekend2_month_Teacher.text = ""
                        tvWeekend3_month_Teacher.text = ""
                        tvWeekend4_month_Teacher.text = ""
                        tvWeekend5_month_Teacher.text = ""
                        tvWeekend6_month_Teacher.text = ""
                        tvWeekend7_month_Teacher.text = ""
                    }
                    2 -> {
                        tvWeekend1_month_Teacher.text = timeList[0]
                        tvWeekend2_month_Teacher.text = timeList[1]
                        tvWeekend3_month_Teacher.text = ""
                        tvWeekend4_month_Teacher.text = ""
                        tvWeekend5_month_Teacher.text = ""
                        tvWeekend6_month_Teacher.text = ""
                        tvWeekend7_month_Teacher.text = ""
                    }
                    3 -> {
                        tvWeekend1_month_Teacher.text = timeList[0]
                        tvWeekend2_month_Teacher.text = timeList[1]
                        tvWeekend3_month_Teacher.text = timeList[2]
                        tvWeekend4_month_Teacher.text = ""
                        tvWeekend5_month_Teacher.text = ""
                        tvWeekend6_month_Teacher.text = ""
                        tvWeekend7_month_Teacher.text = ""
                    }
                    4 -> {
                        tvWeekend1_month_Teacher.text = timeList[0]
                        tvWeekend2_month_Teacher.text = timeList[1]
                        tvWeekend3_month_Teacher.text = timeList[2]
                        tvWeekend4_month_Teacher.text = timeList[3]
                        tvWeekend5_month_Teacher.text = ""
                        tvWeekend6_month_Teacher.text = ""
                        tvWeekend7_month_Teacher.text = ""
                    }
                    5 -> {
                        tvWeekend1_month_Teacher.text = timeList[0]
                        tvWeekend2_month_Teacher.text = timeList[1]
                        tvWeekend3_month_Teacher.text = timeList[2]
                        tvWeekend4_month_Teacher.text = timeList[3]
                        tvWeekend5_month_Teacher.text = timeList[4]
                        tvWeekend6_month_Teacher.text = ""
                        tvWeekend7_month_Teacher.text = ""
                    }
                    6 -> {
                        tvWeekend1_month_Teacher.text = timeList[0]
                        tvWeekend2_month_Teacher.text = timeList[1]
                        tvWeekend3_month_Teacher.text = timeList[2]
                        tvWeekend4_month_Teacher.text = timeList[3]
                        tvWeekend5_month_Teacher.text = timeList[4]
                        tvWeekend6_month_Teacher.text = timeList[5]
                        tvWeekend7_month_Teacher.text = ""
                    }
                }
            }
        }

        if (data.currentWeekNumber == data.weekNumber) {
            // 高亮显示几日是周几
            when (DateUtils.getWeek()) {
                1 -> {
                    tvWeedend7_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend7_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                2 -> {
                    tvWeekend1_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend1_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                3 -> {
                    tvWeedend2_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend2_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                4 -> {
                    tvWeedend3_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend3_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                5 -> {
                    tvWeedend4_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend4_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                6 -> {
                    tvWeedend5_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend5_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                7 -> {
                    tvWeedend6_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend6_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
            }
        } else {
            tvWeedend7_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend7_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend1_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend1_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeedend2_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend2_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeedend3_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend3_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeedend4_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend4_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeedend5_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend5_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeedend6_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend6_month_Teacher.setTextColor(resources.getColor(R.color.login_xdt_et_color))
        }

        // 显示多少周
        currentWeekNum = data.currentWeekNumber
        selectWeekNum = data.weekNumber
        if (isShowWeekend) {
            mTitleTv.text = "返回当前周"
        } else {
            mTitleTv.text = "第" + data.weekNumber.toString() + "周"
        }
        // 一共多少周
        totalWeedNum = data.totalWeekNumber
        for (i in 1..totalWeedNum) {
            weekendList.add(i)
        }
        timeTableWeekendAdapter.setCurrentWeekend(data.weekNumber)
        timeTableWeekendAdapter.notifyDataSetChanged()
        // 课程表主体数据
        if (data.timeTableDetailList != null && data.timeTableDetailList.size > 0) {
            for (i in 0 until data.timeTableDetailList.size) {
                if (data.timeTableDetailList[i].lessonType == 1) {
                    when (data.timeTableDetailList[i].lessonNumber) {
                        // 第几节课
                        1 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[0] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[0], 1)
                            copySubjectMorningList[0] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[0], 1)
                        }
                        2 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[1] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[1], 2)
                            copySubjectMorningList[1] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[1], 2)
                        }
                        3 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[2] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[2], 3)
                            copySubjectMorningList[2] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[2], 3)
                        }
                        4 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[3] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[3], 4)
                            copySubjectMorningList[3] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[3], 4)
                        }
                        5 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[4] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[4], 5)
                            copySubjectMorningList[4] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[4], 5)
                        }
                        6 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[5] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[5], 6)
                            copySubjectMorningList[5] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[5], 6)
                        }
                        7 -> {
                            initSubjectDetailMorning(data.timeTableDetailList[i])
                            subjectMorningList[6] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectMorningList[6], 7)
                            copySubjectMorningList[6] = TimeTableSubjectBean(
                                tempDay1Morning,
                                tempDay2Morning,
                                tempDay3Morning,
                                tempDay4Morning,
                                tempDay5Morning,
                                tempDay6Morning,
                                tempDay7Morning
                            )
                            clearTempDay(copySubjectMorningList[6], 7)
                        }
                    }
                } else {
                    when (data.timeTableDetailList[i].lessonNumber) {
                        // 第几节课
                        1 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[0] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[0], 1)
                            copySubjectNoonList[0] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[0], 1)
                        }
                        2 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[1] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[1], 2)
                            copySubjectNoonList[1] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[1], 2)
                        }
                        3 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[2] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[2], 3)
                            copySubjectNoonList[2] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[2], 3)
                        }
                        4 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[3] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[3], 4)
                            copySubjectNoonList[3] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[3], 4)
                        }
                        5 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[4] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[4], 5)
                            copySubjectNoonList[4] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[4], 5)
                        }
                        6 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[5] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[5], 6)
                            copySubjectNoonList[5] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[5], 6)
                        }
                        7 -> {
                            initSubjectDetailNoon(data.timeTableDetailList[i])
                            subjectNoonList[6] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            // 判断temp临时类里面是不是当前节数的课 不是清空
                            clearTempDay(subjectNoonList[6], 7)
                            copySubjectNoonList[6] = TimeTableSubjectBean(
                                tempDay1Noon,
                                tempDay2Noon,
                                tempDay3Noon,
                                tempDay4Noon,
                                tempDay5Noon,
                                tempDay6Noon,
                                tempDay7Noon
                            )
                            clearTempDay(copySubjectNoonList[6], 7)
                        }
                    }
                }
            }
            subjectTeacherAdapterMorning.notifyDataSetChanged()
            subjectTeacherAdapterNoon.notifyDataSetChanged()
        } else {
            subjectTeacherAdapterMorning.notifyDataSetChanged()
            subjectTeacherAdapterNoon.notifyDataSetChanged()
        }
    }

    private fun clearTempDay(timeTableSubjectBean: TimeTableSubjectBean, lessonNumber: Int) {
        if (timeTableSubjectBean.day1?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day1 = null
        }
        if (timeTableSubjectBean.day2?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day2 = null
        }
        if (timeTableSubjectBean.day3?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day3 = null
        }
        if (timeTableSubjectBean.day4?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day4 = null
        }
        if (timeTableSubjectBean.day5?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day5 = null
        }
        if (timeTableSubjectBean.day6?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day6 = null
        }
        if (timeTableSubjectBean.day7?.lessonNumber != lessonNumber) {
            timeTableSubjectBean.day7 = null
        }
    }

    // 判断是周几的课(上午)
    private fun initSubjectDetailMorning(bean: TimeTableDetailListBean) {
        when (bean.weekDay) {
            1 -> {
                tempDay1Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    1,
                    bean.teacherName
                )
            }
            2 -> {
                tempDay2Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    2,
                    bean.teacherName
                )
            }
            3 -> {
                tempDay3Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    3,
                    bean.teacherName
                )
            }
            4 -> {
                tempDay4Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    4,
                    bean.teacherName
                )
            }
            5 -> {
                tempDay5Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    5,
                    bean.teacherName
                )
            }
            6 -> {
                tempDay6Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    6,
                    bean.teacherName
                )
            }
            7 -> {
                tempDay7Morning = TimeTableRowBean(
                    bean.lessonNumber,
                    1,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    7,
                    bean.teacherName
                )
            }
        }
    }

    // 判断是周几的课(下午)
    private fun initSubjectDetailNoon(bean: TimeTableDetailListBean) {
        when (bean.weekDay) {
            1 -> {
                tempDay1Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    1,
                    bean.teacherName
                )
            }
            2 -> {
                tempDay2Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    2,
                    bean.teacherName
                )
            }
            3 -> {
                tempDay3Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    3,
                    bean.teacherName
                )
            }
            4 -> {
                tempDay4Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    4,
                    bean.teacherName
                )
            }
            5 -> {
                tempDay5Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    5,
                    bean.teacherName
                )
            }
            6 -> {
                tempDay6Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    6,
                    bean.teacherName
                )
            }
            7 -> {
                tempDay7Noon = TimeTableRowBean(
                    bean.lessonNumber,
                    2,
                    bean.subjectId,
                    bean.subjectName,
                    bean.teacherId.toString(),
                    7,
                    bean.teacherName
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                100 -> {
                    // 获取课程表设置接口
                    mPresenter.getSubjectSetting(classId)
                    // 获取班级科目列表
                    mPresenter.getClassSubjectList(classId)
                }
            }
        }
    }

    // 获取班级科目列表
    override fun getClassSubjectList(dataList: MutableList<ClassChooseSubjectBean>?) {
        subjectChooseList.clear()
        if (dataList != null && dataList.size > 0) {
            for (item in dataList) {
                if (item.isSelected) {
                    subjectChooseList.add(item)
                }
            }
            selectSubjectTeacherAdapter.notifyDataSetChanged()
        }
    }

    // 课程表详情编辑接口回调
    override fun upDataTimeTableDetail(msg: String) {
        ToastUtils.showMsg(this, msg)
        isEdit = false
        subjectTeacherAdapterMorning.setIsEdit(isEdit)
        subjectTeacherAdapterNoon.setIsEdit(isEdit)
        FAB.setVisible(true)
        rlTimeTable.setVisible(true)
        rlEditTimeTable.setVisible(false)
        rvSelected.setVisible(false)
        mPresenter.getTimeTableInfo(classId, "", false)
    }
}