package com.kapok.apps.maple.xdt.timetable.activity.timetable_parent

import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.adapter.SubjectTeacherAdapter
import com.kapok.apps.maple.xdt.timetable.adapter.TimeTableWeekendAdapter
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableDetailListBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean.TimeTableRowBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean.TimeTableSubjectBean
import com.kapok.apps.maple.xdt.timetable.presenter.TimeTableParentPresenter
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableParentView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_time_table_parent.*
import kotlinx.android.synthetic.main.activity_time_table_teacher.*
import java.util.*

/**
 * 课程表展示（家长）
 */
class TimeTableParentActivity : BaseMVPActivity<TimeTableParentPresenter>(), TimeTableParentView {
    // 课程数据List(上午)
    private var subjectMorningList: MutableList<TimeTableSubjectBean> = arrayListOf()
    // 课程数据List(下午)
    private var subjectNoonList: MutableList<TimeTableSubjectBean> = arrayListOf()

    // 上午课程Adapter
    lateinit var subjectParentAdapterMorning: SubjectTeacherAdapter
    // 上午课程数量
    private var morningClassNum: Int = -1
    // 下午课程Adapter
    lateinit var subjectParentAdapterNoon: SubjectTeacherAdapter
    // 下午课程数量
    private var noonClassNum: Int = -1

    // 副标题
    private lateinit var timeTableName: String
    // 是否显示周数条
    private var isShowWeekend = false
    // 当前周
    private var currentWeekNum = -1
    // 一共多少周
    private var totalWeedNum = -1
    // 一共多少周列表
    private var weekendList: MutableList<Int> = arrayListOf()
    // 一共多少周列表Adapter
    private lateinit var timeTableWeekendAdapter: TimeTableWeekendAdapter
    // 当前选中的周
    private var selectWeekNum = ""

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
        setContentView(R.layout.activity_time_table_parent)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = TimeTableParentPresenter(this)
        mPresenter.mView = this
        classId = intent.getIntExtra("classId", -1)
        // 页面初始化配置
        initData()
    }

    private fun initData() {
        // 获取课程表设置接口
        mPresenter.getSubjectSetting(classId)

        // 获取当前月份
        val currentMonth = DateUtils.getMonth()
        tvWeekendMonthParent.text = currentMonth.toString()
        // 显示有多少周的列表
        timeTableWeekendAdapter = TimeTableWeekendAdapter(this, weekendList)
        rvTimeTableWeekendParent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTimeTableWeekendParent.adapter = timeTableWeekendAdapter

        // 上午列表
        subjectParentAdapterMorning = SubjectTeacherAdapter(this, subjectMorningList, false)
        rvMorningParent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMorningParent.adapter = subjectParentAdapterMorning

        // 下午列表
        subjectParentAdapterNoon = SubjectTeacherAdapter(this, subjectNoonList, false)
        rvNoonParent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvNoonParent.adapter = subjectParentAdapterNoon
    }

    private fun initListener() {
        // 标题点击事件
        mTitleTvParent.setOnClickListener {
            if (isShowWeekend) {
                mTitleTvParent.text = "第" + currentWeekNum.toString() + "周"
                rvTimeTableWeekendParent.scrollToPosition(0)
                mPresenter.getTimeTableInfo(classId,currentWeekNum.toString(),false)
                isShowWeekend = false
                rlTimeTableWeekendParent.setVisible(false)
            } else {
                mTitleTvParent.text = "返回当前周"
                isShowWeekend = true
                mPresenter.getTimeTableInfo(classId,currentWeekNum.toString(),false)
                rlTimeTableWeekendParent.setVisible(true)
            }
        }
        // 有多少周列表点击回调（返回点击的周）
        timeTableWeekendAdapter.setSelectLessonTimeListener(object :
            TimeTableWeekendAdapter.SelectTimeTableWeekendInterface {
            override fun onSelectData(currentWeekend: Int) {
                selectWeekNum = currentWeekend.toString()
                mPresenter.getTimeTableInfo(classId, currentWeekend.toString(), true)
            }
        })
        // 返回
        mLeftIvParent.setOnClickListener { finish() }
        // 课程表主列表(上午)点击回调
        subjectParentAdapterMorning.setOnSubjectItemClickListener(object :
            SubjectTeacherAdapter.OnSubjectItemClickListener {
            override fun onSubjectItemClick(weekend: Int, subjectNum: Int) {
                val intent = Intent(this@TimeTableParentActivity,SubjectDetailActivity::class.java)
                intent.putExtra("weekend",weekend)
                intent.putExtra("selectWeekend",selectWeekNum)
                intent.putExtra("selectNum",subjectNum)
                intent.putExtra("classId",classId)
                startActivity(intent)
            }
        })
        // 课程表主列表(下午)点击回调
        subjectParentAdapterNoon.setOnSubjectItemClickListener(object :
            SubjectTeacherAdapter.OnSubjectItemClickListener {
            override fun onSubjectItemClick(weekend: Int, subjectNum: Int) {
                val intent = Intent(this@TimeTableParentActivity,SubjectDetailActivity::class.java)
                intent.putExtra("weekend",weekend)
                intent.putExtra("selectWeekend",selectWeekNum)
                intent.putExtra("selectNum",subjectNum)
                intent.putExtra("classId",classId)
                startActivity(intent)
            }
        })
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
                mSubTitleParent.setVisible(true)
                mSubTitleParent.text = timeTableName
            } else {
                mSubTitleParent.setVisible(false)
            }
            // 获取课程表
            mPresenter.getTimeTableInfo(classId, "", true)
        } else {
            tvRestParent.setVisible(false)
            ToastUtils.showMsg(this, "该班级还没有设置课程表")
            mTitleTvParent.text = "课程表"
            mSubTitleParent.setVisible(false)
        }
    }

    // 获取课程表回调
    override fun getTimeTableSubject(data: TimeTableInfoBean) {
        tvRestParent.setVisible(true)
        weekendList.clear()
        // 上午数据配置
        subjectMorningList.clear()
        tempDay1Morning = null
        tempDay2Morning = null
        tempDay3Morning = null
        tempDay4Morning = null
        tempDay5Morning = null
        tempDay6Morning = null
        tempDay7Morning = null
        // 下午数据配置
        subjectNoonList.clear()
        tempDay1Noon = null
        tempDay2Noon = null
        tempDay3Noon = null
        tempDay4Noon = null
        tempDay5Noon = null
        tempDay6Noon = null
        tempDay7Noon = null

        for (i in 0 until morningClassNum) {
            subjectMorningList.add(TimeTableSubjectBean())
        }
        for (i in 0 until noonClassNum) {
            subjectNoonList.add(TimeTableSubjectBean())
        }
        // 显示日期
        val beginData = data.beginDate
        val endData = data.endDate
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
        tvWeekendMonthParent.text = timeList[0].substring(0,2)

        if (7 <= timeList.size) {
            tvWeekend1_monthParent.text = timeList[0]
            tvWeekend2_monthParent.text = timeList[1]
            tvWeekend3_monthParent.text = timeList[2]
            tvWeekend4_monthParent.text = timeList[3]
            tvWeekend5_monthParent.text = timeList[4]
            tvWeekend6_monthParent.text = timeList[5]
            tvWeekend7_monthParent.text = timeList[6]
        } else {
            // 第一周
            if (1 == data.weekNumber) {
                when(tempWeek) {
                    2 -> {
                        tvWeekend1_monthParent.text = ""
                        tvWeekend2_monthParent.text = timeList[0]
                        tvWeekend3_monthParent.text = timeList[1]
                        tvWeekend4_monthParent.text = timeList[2]
                        tvWeekend5_monthParent.text = timeList[3]
                        tvWeekend6_monthParent.text = timeList[4]
                        tvWeekend7_monthParent.text = timeList[5]
                    }
                    3 -> {
                        tvWeekend1_monthParent.text = ""
                        tvWeekend2_monthParent.text = ""
                        tvWeekend3_monthParent.text = timeList[0]
                        tvWeekend4_monthParent.text = timeList[1]
                        tvWeekend5_monthParent.text = timeList[2]
                        tvWeekend6_monthParent.text = timeList[3]
                        tvWeekend7_monthParent.text = timeList[4]
                    }
                    4 -> {
                        tvWeekend1_monthParent.text = ""
                        tvWeekend2_monthParent.text = ""
                        tvWeekend3_monthParent.text = ""
                        tvWeekend4_monthParent.text = timeList[0]
                        tvWeekend5_monthParent.text = timeList[1]
                        tvWeekend6_monthParent.text = timeList[2]
                        tvWeekend7_monthParent.text = timeList[3]
                    }
                    5 -> {
                        tvWeekend1_monthParent.text = ""
                        tvWeekend2_monthParent.text = ""
                        tvWeekend3_monthParent.text = ""
                        tvWeekend4_monthParent.text = ""
                        tvWeekend5_monthParent.text = timeList[0]
                        tvWeekend6_monthParent.text = timeList[1]
                        tvWeekend7_monthParent.text = timeList[2]
                    }
                    6 -> {
                        tvWeekend1_monthParent.text = ""
                        tvWeekend2_monthParent.text = ""
                        tvWeekend3_monthParent.text = ""
                        tvWeekend4_monthParent.text = ""
                        tvWeekend5_monthParent.text = ""
                        tvWeekend6_monthParent.text = timeList[0]
                        tvWeekend7_monthParent.text = timeList[1]
                    }
                    7 -> {
                        tvWeekend1_monthParent.text = ""
                        tvWeekend2_monthParent.text = ""
                        tvWeekend3_monthParent.text = ""
                        tvWeekend4_monthParent.text = ""
                        tvWeekend5_monthParent.text = ""
                        tvWeekend6_monthParent.text = ""
                        tvWeekend7_monthParent.text = timeList[0]
                    }
                }
                // 最后一周
            } else if (data.weekNumber == data.totalWeekNumber) {
                when(tempLastWeek) {
                    1 -> {
                        tvWeekend1_monthParent.text = timeList[0]
                        tvWeekend2_monthParent.text = ""
                        tvWeekend3_monthParent.text = ""
                        tvWeekend4_monthParent.text = ""
                        tvWeekend5_monthParent.text = ""
                        tvWeekend6_monthParent.text = ""
                        tvWeekend7_monthParent.text = ""
                    }
                    2 -> {
                        tvWeekend1_monthParent.text = timeList[0]
                        tvWeekend2_monthParent.text = timeList[1]
                        tvWeekend3_monthParent.text = ""
                        tvWeekend4_monthParent.text = ""
                        tvWeekend5_monthParent.text = ""
                        tvWeekend6_monthParent.text = ""
                        tvWeekend7_monthParent.text = ""
                    }
                    3 -> {
                        tvWeekend1_monthParent.text = timeList[0]
                        tvWeekend2_monthParent.text = timeList[1]
                        tvWeekend3_monthParent.text = timeList[2]
                        tvWeekend4_monthParent.text = ""
                        tvWeekend5_monthParent.text = ""
                        tvWeekend6_monthParent.text = ""
                        tvWeekend7_monthParent.text = ""
                    }
                    4 -> {
                        tvWeekend1_monthParent.text = timeList[0]
                        tvWeekend2_monthParent.text = timeList[1]
                        tvWeekend3_monthParent.text = timeList[2]
                        tvWeekend4_monthParent.text = timeList[3]
                        tvWeekend5_monthParent.text = ""
                        tvWeekend6_monthParent.text = ""
                        tvWeekend7_monthParent.text = ""
                    }
                    5 -> {
                        tvWeekend1_monthParent.text = timeList[0]
                        tvWeekend2_monthParent.text = timeList[1]
                        tvWeekend3_monthParent.text = timeList[2]
                        tvWeekend4_monthParent.text = timeList[3]
                        tvWeekend5_monthParent.text = timeList[4]
                        tvWeekend6_monthParent.text = ""
                        tvWeekend7_monthParent.text = ""
                    }
                    6 -> {
                        tvWeekend1_monthParent.text = timeList[0]
                        tvWeekend2_monthParent.text = timeList[1]
                        tvWeekend3_monthParent.text = timeList[2]
                        tvWeekend4_monthParent.text = timeList[3]
                        tvWeekend5_monthParent.text = timeList[4]
                        tvWeekend6_monthParent.text = timeList[5]
                        tvWeekend7_monthParent.text = ""
                    }
                }
            }
        }

        // 高亮显示几日是周几
        if (data.currentWeekNumber == data.weekNumber) {
            when (DateUtils.getWeek()) {
                1 -> {
                    tvWeekend7Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend7_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                2 -> {
                    tvWeekend1Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend1_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                3 -> {
                    tvWeekend2Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend2_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                4 -> {
                    tvWeekend3Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend3_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                5 -> {
                    tvWeekend4Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend4_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                6 -> {
                    tvWeekend5Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend5_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
                7 -> {
                    tvWeekend6Parent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    tvWeekend6_monthParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                }
            }
        } else {
            tvWeekend7Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend7_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend1Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend1_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend2Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend2_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend3Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend3_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend4Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend4_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend5Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend5_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend6Parent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
            tvWeekend7_monthParent.setTextColor(resources.getColor(R.color.login_xdt_et_color))
        }
        // 显示多少周
        currentWeekNum = data.currentWeekNumber
        selectWeekNum = data.weekNumber.toString()
        if (isShowWeekend) {
            mTitleTvParent.text = "返回当前周"
        } else {
            mTitleTvParent.text = "第" + data.currentWeekNumber.toString() + "周"
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
                            clearTempDay(subjectMorningList[0],1)
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
                            clearTempDay(subjectMorningList[1],2)
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
                            clearTempDay(subjectMorningList[2],3)
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
                            clearTempDay(subjectMorningList[3],4)
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
                            clearTempDay(subjectMorningList[4],5)
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
                            clearTempDay(subjectMorningList[5],6)
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
                            clearTempDay(subjectMorningList[6],7)
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
                            clearTempDay(subjectNoonList[0],1)
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
                            clearTempDay(subjectNoonList[1],2)
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
                            clearTempDay(subjectNoonList[2],3)
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
                            clearTempDay(subjectNoonList[3],4)
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
                            clearTempDay(subjectNoonList[4],5)
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
                            clearTempDay(subjectNoonList[5],6)
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
                            clearTempDay(subjectNoonList[6],7)
                        }
                    }
                }
            }
            subjectParentAdapterMorning.notifyDataSetChanged()
            subjectParentAdapterNoon.notifyDataSetChanged()
        } else {
            subjectParentAdapterMorning.notifyDataSetChanged()
            subjectParentAdapterNoon.notifyDataSetChanged()
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
}