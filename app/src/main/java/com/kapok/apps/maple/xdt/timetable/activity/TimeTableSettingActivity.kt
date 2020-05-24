package com.kapok.apps.maple.xdt.timetable.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.activity.timetable_teacher.TimeTableTeacherActivity
import com.kapok.apps.maple.xdt.timetable.presenter.TimeTableSettingPresenter
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableSettingView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CustomDataDialog
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kapok.apps.maple.xdt.timetable.adapter.LessonsSettingMorningAdapter
import com.kapok.apps.maple.xdt.timetable.adapter.LessonsSettingNoonAdapter
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingDetailSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.ToastUtils
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_time_table_setting.*

/**
 *  课程表设置页面
 *  fanjie
 */
class TimeTableSettingActivity : BaseMVPActivity<TimeTableSettingPresenter>(), TimeTableSettingView {
    private lateinit var customDataDialog: CustomDataDialog
    // 选中的 年/月/日（开始）
    private lateinit var mSelectYearStart: String
    private lateinit var mSelectMonthStart: String
    private lateinit var mSelectDayStart: String
    private var hasSelectStart: Boolean = false
    // 选中的 年/月/日（结束）
    private lateinit var mSelectYearEnd: String
    private lateinit var mSelectMonthEnd: String
    private lateinit var mSelectDayEnd: String
    private var hasSelectEnd: Boolean = false
    // 上午课程数Adapter
    private lateinit var mMorningAdapter: LessonsSettingMorningAdapter
    private var mMorningSubjectNum: Int = 0
    // 下午课程数Adapter
    private lateinit var mNoonAdapter: LessonsSettingNoonAdapter
    private var mNoonSubjectNum: Int = 0
    // 存储课程集合(上午)
    private var templessonsListMorning: MutableList<TimeTableSettingDetailSubjectBean> = mutableListOf()
    private var selectMorningList = mutableListOf<TimeTableSettingDetailSubjectBean>()
    // 存储课程集合(下午)
    private var templessonsListNoon: MutableList<TimeTableSettingDetailSubjectBean> = mutableListOf()
    private var selectNoonList = mutableListOf<TimeTableSettingDetailSubjectBean>()
    // 存储课程总集合
    private lateinit var selectTotalList : MutableList<TimeTableSettingDetailSubjectBean>
    // 存储课程Map(用于给Adapter 传获得的集合)
    private var lessonsMapMorning: MutableMap<String, String> = mutableMapOf()
    private var lessonsMapNoon: MutableMap<String, String> = mutableMapOf()
    // 已选中的班级课程列表
    private lateinit var classSubjectList: MutableList<String>
    // 学期开始/结束 日期
    private lateinit var beginData: String
    private lateinit var endData: String
    private var classId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table_setting)
        initView()
        initData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        // 获取班级科目列表
        mPresenter.getClassSubjectList(classId)
    }

    private fun initData() {
        // 获取已设置的课程表设置
        mPresenter.getSubjectSetting(classId)
    }

    private fun initListener() {
        // 左上角 返回
        mLeftIvTimeTableSetting.setOnClickListener {
            // 调用设置接口 提交设置信息
            setResult(Activity.RESULT_OK)
            finish()
        }
        // 保存
        tv_timetable_save.setOnClickListener {
            selectTotalList.clear()
            for (item in selectMorningList) {
                selectTotalList.add(item)
            }
            for (item in selectNoonList) {
                selectTotalList.add(item)
            }
            // 保存接口
            if (etTimeTableName.text.toString().isNotEmpty() && hasSelectStart && hasSelectEnd && selectMorningList.size == mMorningSubjectNum && selectNoonList.size == mNoonSubjectNum) {
                mPresenter.saveSubjectSetting(
                    mMorningSubjectNum,
                    beginData,
                    classId,
                    endData,
                    mNoonSubjectNum,
                    etTimeTableName.text.toString(),
                    selectTotalList
                )
            } else {
                ToastUtils.showMsg(this, "您还有设置信息未填写")
            }
        }
        // 学期开始日期
        rl_timetable_start.setOnClickListener {
            customDataDialog = CustomDataDialog(this@TimeTableSettingActivity, R.style.BottomDialog)
            customDataDialog.setTitle("学期开始日期")
            if (hasSelectStart) {
                customDataDialog.setSelectedTime(mSelectYearStart, mSelectMonthStart, mSelectDayStart)
            } else {
                customDataDialog.getCurrentTime()
            }
            customDataDialog.show()
            customDataDialog.setOnselectDataListener(object : CustomDataDialog.SelectDataListener {
                override fun selectData(year: String, month: String, day: String) {
                    hasSelectStart = true
                    mSelectYearStart = year
                    mSelectMonthStart = (if (Integer.valueOf(month) > 9) month else "0$month").toString()
                    mSelectDayStart = (if (Integer.valueOf(day) > 9) day else "0$day").toString()
                    beginData = "$mSelectYearStart-$mSelectMonthStart-$mSelectDayStart"
                    tv_timetable_start.text = beginData
                }
            })
        }
        // 学期结束日期
        rl_timetable_end.setOnClickListener {
            customDataDialog = CustomDataDialog(this@TimeTableSettingActivity, R.style.BottomDialog)
            customDataDialog.setTitle("学期结束日期")
            if (hasSelectEnd) {
                customDataDialog.setSelectedTime(mSelectYearEnd, mSelectMonthEnd, mSelectDayEnd)
            } else {
                customDataDialog.getCurrentTime()
            }
            customDataDialog.show()
            customDataDialog.setOnselectDataListener(object : CustomDataDialog.SelectDataListener {
                override fun selectData(year: String, month: String, day: String) {
                    hasSelectEnd = true
                    mSelectYearEnd = year
                    mSelectMonthEnd = (if (Integer.valueOf(month) > 9) month else "0$month").toString()
                    mSelectDayEnd = (if (Integer.valueOf(day) > 9) day else "0$day").toString()
                    endData = "$mSelectYearEnd-$mSelectMonthEnd-$mSelectDayEnd"
                    tv_timetable_end.text = endData
                }
            })
        }
        // 上午几节课
        rg_timetable_morning.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.bt_timetable_lesson0_morning -> {
                    mMorningSubjectNum = 0
                    checkSelectSubjectNumMorning()
                    rv_morning_lesson.setVisible(false)
                }
                R.id.bt_timetable_lesson1_morning -> {
                    mMorningSubjectNum = 1
                    checkSelectSubjectNumMorning()
                    rv_morning_lesson.setVisible(true)
                    val arrayList = arrayListOf("第1节课")
                    mMorningAdapter.setLessonsMap(lessonsMapMorning)
                    mMorningAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson2_morning -> {
                    mMorningSubjectNum = 2
                    checkSelectSubjectNumMorning()
                    val arrayList = arrayListOf("第1节课", "第2节课")
                    rv_morning_lesson.setVisible(true)
                    mMorningAdapter.setLessonsMap(lessonsMapMorning)
                    mMorningAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson3_morning -> {
                    mMorningSubjectNum = 3
                    checkSelectSubjectNumMorning()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课")
                    rv_morning_lesson.setVisible(true)
                    mMorningAdapter.setLessonsMap(lessonsMapMorning)
                    mMorningAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson4_morning -> {
                    mMorningSubjectNum = 4
                    checkSelectSubjectNumMorning()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课", "第4节课")
                    rv_morning_lesson.setVisible(true)
                    mMorningAdapter.setLessonsMap(lessonsMapMorning)
                    mMorningAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson5_morning -> {
                    mMorningSubjectNum = 5
                    checkSelectSubjectNumMorning()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课", "第4节课", "第5节课")
                    rv_morning_lesson.setVisible(true)
                    mMorningAdapter.setLessonsMap(lessonsMapMorning)
                    mMorningAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson6_morning -> {
                    mMorningSubjectNum = 6
                    checkSelectSubjectNumMorning()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课", "第4节课", "第5节课", "第6节课")
                    rv_morning_lesson.setVisible(true)
                    mMorningAdapter.setLessonsMap(lessonsMapMorning)
                    mMorningAdapter.setNewData(arrayList)
                }
            }
        }
        // 选择课节数弹窗(上午)
        mMorningAdapter.setSelectLessonTimeListener(object : LessonsSettingMorningAdapter.SelectLessonTimeInterface {
            override fun onShowSelectData(lessonMap: MutableMap<String, String>) {
                selectMorningList.clear()
                templessonsListMorning.clear()
                for (key in lessonMap.keys) {
                    when (key) {
                        "第1节课" -> {
                            val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                            val minute = lessonMap[key]?.split("~")?.get(1)
                            if (minute != null) {
                                val tempH = DateUtils.formatTurnSecond(hour)
                                val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                templessonsListMorning.add(TimeTableSettingDetailSubjectBean(hour, result, 1, 1))
                            }
                        }
                        "第2节课" -> {
                            val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                            val minute = lessonMap[key]?.split("~")?.get(1)
                            if (minute != null) {
                                val tempH = DateUtils.formatTurnSecond(hour)
                                val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                templessonsListMorning.add(TimeTableSettingDetailSubjectBean(hour, result, 2, 1))
                            }
                        }
                        "第3节课" -> {
                            val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                            val minute = lessonMap[key]?.split("~")?.get(1)
                            if (minute != null) {
                                val tempH = DateUtils.formatTurnSecond(hour)
                                val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                templessonsListMorning.add(TimeTableSettingDetailSubjectBean(hour, result, 3, 1))
                            }
                        }
                        "第4节课" -> {
                            val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                            val minute = lessonMap[key]?.split("~")?.get(1)
                            if (minute != null) {
                                val tempH = DateUtils.formatTurnSecond(hour)
                                val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                templessonsListMorning.add(TimeTableSettingDetailSubjectBean(hour, result, 4, 1))
                            }
                        }
                        "第5节课" -> {
                            val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                            val minute = lessonMap[key]?.split("~")?.get(1)
                            if (minute != null) {
                                val tempH = DateUtils.formatTurnSecond(hour)
                                val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                templessonsListMorning.add(TimeTableSettingDetailSubjectBean(hour, result, 5, 1))
                            }
                        }
                        "第6节课" -> {
                            val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                            val minute = lessonMap[key]?.split("~")?.get(1)
                            if (minute != null) {
                                val tempH = DateUtils.formatTurnSecond(hour)
                                val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                templessonsListMorning.add(TimeTableSettingDetailSubjectBean(hour, result, 6, 1))
                            }
                        }
                    }
                }
                // 检测列表中的时间 和 选中的课程数 是否一致
                if (templessonsListMorning.size > mMorningSubjectNum) {
                    for (item in templessonsListMorning) {
                        if (item.lessonNumber <= mMorningSubjectNum) {
                            selectMorningList.add(item)
                        }
                    }
                } else {
                    selectMorningList.addAll(templessonsListMorning)
                }
            }
        })

        // 下午几节课
        rg_timetable_noon.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.bt_timetable_lesson0_noon -> {
                    mNoonSubjectNum = 0
                    checkSelectSubjectNumNoon()
                    rv_noon_lesson.setVisible(false)
                }
                R.id.bt_timetable_lesson1_noon -> {
                    mNoonSubjectNum = 1
                    checkSelectSubjectNumNoon()
                    val arrayList = arrayListOf("第1节课")
                    rv_noon_lesson.setVisible(true)
                    mNoonAdapter.setLessonsMap(lessonsMapNoon)
                    mNoonAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson2_noon -> {
                    mNoonSubjectNum = 2
                    checkSelectSubjectNumNoon()
                    val arrayList = arrayListOf("第1节课", "第2节课")
                    rv_noon_lesson.setVisible(true)
                    mNoonAdapter.setLessonsMap(lessonsMapNoon)
                    mNoonAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson3_noon -> {
                    mNoonSubjectNum = 3
                    checkSelectSubjectNumNoon()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课")
                    rv_noon_lesson.setVisible(true)
                    mNoonAdapter.setLessonsMap(lessonsMapNoon)
                    mNoonAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson4_noon -> {
                    mNoonSubjectNum = 4
                    checkSelectSubjectNumNoon()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课", "第4节课")
                    rv_noon_lesson.setVisible(true)
                    mNoonAdapter.setLessonsMap(lessonsMapNoon)
                    mNoonAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson5_noon -> {
                    mNoonSubjectNum = 5
                    checkSelectSubjectNumNoon()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课", "第4节课", "第5节课")
                    rv_noon_lesson.setVisible(true)
                    mNoonAdapter.setLessonsMap(lessonsMapNoon)
                    mNoonAdapter.setNewData(arrayList)
                }
                R.id.bt_timetable_lesson6_noon -> {
                    mNoonSubjectNum = 6
                    checkSelectSubjectNumNoon()
                    val arrayList = arrayListOf("第1节课", "第2节课", "第3节课", "第4节课", "第5节课", "第6节课")
                    rv_noon_lesson.setVisible(true)
                    mNoonAdapter.setLessonsMap(lessonsMapNoon)
                    mNoonAdapter.setNewData(arrayList)
                }
            }
            // 选择课节数弹窗(下午)
            mNoonAdapter.setSelectLessonTimeListener(object : LessonsSettingNoonAdapter.SelectLessonTimeInterface {
                override fun onShowSelectData(lessonMap: MutableMap<String, String>) {
                    selectNoonList.clear()
                    templessonsListNoon.clear()
                    for (key in lessonMap.keys) {
                        when (key) {
                            "第1节课" -> {
                                val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                                val minute = lessonMap[key]?.split("~")?.get(1)
                                if (minute != null) {
                                    val tempH = DateUtils.formatTurnSecond(hour)
                                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                    templessonsListNoon.add(TimeTableSettingDetailSubjectBean(hour, result, 1, 2))
                                }
                            }
                            "第2节课" -> {
                                val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                                val minute = lessonMap[key]?.split("~")?.get(1)
                                if (minute != null) {
                                    val tempH = DateUtils.formatTurnSecond(hour)
                                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                    templessonsListNoon.add(TimeTableSettingDetailSubjectBean(hour, result, 2, 2))
                                }
                            }
                            "第3节课" -> {
                                val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                                val minute = lessonMap[key]?.split("~")?.get(1)
                                if (minute != null) {
                                    val tempH = DateUtils.formatTurnSecond(hour)
                                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                    templessonsListNoon.add(TimeTableSettingDetailSubjectBean(hour, result, 3, 2))
                                }
                            }
                            "第4节课" -> {
                                val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                                val minute = lessonMap[key]?.split("~")?.get(1)
                                if (minute != null) {
                                    val tempH = DateUtils.formatTurnSecond(hour)
                                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                    templessonsListNoon.add(TimeTableSettingDetailSubjectBean(hour, result, 4, 2))
                                }
                            }
                            "第5节课" -> {
                                val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                                val minute = lessonMap[key]?.split("~")?.get(1)
                                if (minute != null) {
                                    val tempH = DateUtils.formatTurnSecond(hour)
                                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                    templessonsListNoon.add(TimeTableSettingDetailSubjectBean(hour, result, 5, 2))
                                }
                            }
                            "第6节课" -> {
                                val hour = lessonMap[key]?.split("~")?.get(0).toString().trim()
                                val minute = lessonMap[key]?.split("~")?.get(1)
                                if (minute != null) {
                                    val tempH = DateUtils.formatTurnSecond(hour)
                                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                                    templessonsListNoon.add(TimeTableSettingDetailSubjectBean(hour, result, 6, 2))
                                }
                            }
                        }
                    }
                    // 检测列表中的时间 和 选中的课程数 是否一致
                    if (templessonsListNoon.size > mNoonSubjectNum) {
                        for (item in templessonsListNoon) {
                            if (item.lessonNumber <= mNoonSubjectNum) {
                                selectNoonList.add(item)
                            }
                        }
                    } else {
                        selectNoonList.addAll(templessonsListNoon)
                    }
                }
            })
        }
        // 本学期科目 添加/编辑按钮
        tv_timetable_addlesson.setOnClickListener {
            // 进入选择课程页面
            val intent = Intent(this@TimeTableSettingActivity, TimeTableChooseSubjectActivity::class.java)
            intent.putExtra("classId",classId)
            startActivity(intent)
        }

    }

    // 检测列表中的时间 和 选中的课程数 是否一致
    private fun checkSelectSubjectNumMorning() {
        selectMorningList.clear()
        if (templessonsListMorning.size > 0) {
            if (templessonsListMorning.size > mMorningSubjectNum) {
                for (item in templessonsListMorning) {
                    if (item.lessonNumber <= mMorningSubjectNum) {
                        selectMorningList.add(item)
                    }
                }
            } else {
                selectMorningList.addAll(templessonsListMorning)
            }
        }
    }

    // 检测列表中的时间 和 选中的课程数 是否一致
    private fun checkSelectSubjectNumNoon() {
        selectNoonList.clear()
        if (templessonsListNoon.size > 0) {
            if (templessonsListNoon.size > mNoonSubjectNum) {
                for (item in templessonsListNoon) {
                    if (item.lessonNumber <= mNoonSubjectNum) {
                        selectNoonList.add(item)
                    }
                }
            } else {
                selectNoonList.addAll(templessonsListNoon)
            }
        }
    }

    private fun initView() {
        mPresenter = TimeTableSettingPresenter(this)
        mPresenter.mView = this
        classId = intent.getIntExtra("classId",-1)
        // 课程数量RecyclerView(上午)
        mMorningAdapter = LessonsSettingMorningAdapter(this, arrayListOf())
        mMorningAdapter.setLessonsMap(lessonsMapMorning)
        rv_morning_lesson.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_morning_lesson.adapter = mMorningAdapter
        rv_morning_lesson.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
        // 课程选择数量RecyclerView(下午)
        mNoonAdapter = LessonsSettingNoonAdapter(this, arrayListOf())
        mNoonAdapter.setLessonsMap(lessonsMapNoon)
        rv_noon_lesson.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_noon_lesson.adapter = mNoonAdapter
        rv_noon_lesson.addItemDecoration(
            RecycleViewDivider(
                this, RecycleViewDivider.VERTICAL, Dp2pxUtils.dp2px(this, 1)
            )
        )
        classSubjectList = mutableListOf()
        selectTotalList = mutableListOf()
    }

    // 获取 已设置信息数据
    override fun getSettingInfo(data: TimeTableSettingInfoBean) {
        // 上午(下午)课程数
        selectTotalList.clear()
        selectMorningList.clear()
        if (data.beginDate.isNotEmpty()) {
            // beginData = DateUtils.longToString(data.beginDate.toLong(),"yyyy-MM-dd")
            beginData = data.beginDate
            // beginData = data.beginDate.substring(0,data.beginDate.indexOf("T"))
            val str = beginData.split("-")
            mSelectYearStart = str[0]
            mSelectMonthStart = str[1]
            mSelectDayStart = str[2]
            tv_timetable_start.text = beginData
            hasSelectStart = true
        }
        if (data.endDate.isNotEmpty()) {
            // endData = DateUtils.longToString(data.endDate.toLong(),"yyyy-MM-dd")
            endData = data.endDate
            // endData = data.endDate.substring(0,data.endDate.indexOf("T"))
            val str = endData.split("-")
            mSelectYearEnd = str[0]
            mSelectMonthEnd = str[1]
            mSelectDayEnd = str[2]
            tv_timetable_end.text = endData
            hasSelectEnd = true
        }
        classId = data.classId
        etTimeTableName.text = SpannableStringBuilder(data.timeTableName)
        if (data.timetableConfigDetailList != null && data.timetableConfigDetailList.size > 0) {
            selectTotalList = data.timetableConfigDetailList
            for (item in data.timetableConfigDetailList) {
                if (item.lessonType == 1) {
                    selectMorningList.add(item)
                    templessonsListMorning.add(item)
                } else {
                    selectNoonList.add(item)
                    templessonsListNoon.add(item)
                }
            }
        }
        for (item in selectMorningList) {
            val tempH = DateUtils.formatTurnSecond(item.lessonBegintime)
            val result = DateUtils.formatTurnSecond(item.lessonEndtime)
            var tempM = DateUtils.changeToTime((result - tempH).toInt()) + "分钟"

            val second = (result - tempH).toInt()
            val temp = (second % 3600)
            if (second > 3600) {
                if (temp != 0) {
                    if (temp > 60) {
                        tempM = (temp / 60).toString() + "分钟"
                    }
                }
            } else {
                tempM = (temp / 60).toString() + "分钟"
            }

            when(item.lessonNumber) {
                1 -> lessonsMapMorning["第1节课"] = item.lessonBegintime + "~" + tempM
                2 -> lessonsMapMorning["第2节课"] = item.lessonBegintime + "~" + tempM
                3 -> lessonsMapMorning["第3节课"] = item.lessonBegintime + "~" + tempM
                4 -> lessonsMapMorning["第4节课"] = item.lessonBegintime + "~" + tempM
                5 -> lessonsMapMorning["第5节课"] = item.lessonBegintime + "~" + tempM
                6 -> lessonsMapMorning["第6节课"] = item.lessonBegintime + "~" + tempM
            }
        }

        for (item in selectNoonList) {
            val tempH = DateUtils.formatTurnSecond(item.lessonBegintime)
            val result = DateUtils.formatTurnSecond(item.lessonEndtime)
            var tempM = DateUtils.changeToTime((result - tempH).toInt()) + "分钟"

            val second = (result - tempH).toInt()
            val temp = (second % 3600)
            if (second > 3600) {
                if (temp != 0) {
                    if (temp > 60) {
                        tempM = (temp / 60).toString() + "分钟"
                    }
                }
            } else {
                tempM = (temp / 60).toString() + "分钟"
            }

            when(item.lessonNumber) {
                1 -> lessonsMapNoon["第1节课"] = item.lessonBegintime + "~" + tempM
                2 -> lessonsMapNoon["第2节课"] = item.lessonBegintime + "~" + tempM
                3 -> lessonsMapNoon["第3节课"] = item.lessonBegintime + "~" + tempM
                4 -> lessonsMapNoon["第4节课"] = item.lessonBegintime + "~" + tempM
                5 -> lessonsMapNoon["第5节课"] = item.lessonBegintime + "~" + tempM
                6 -> lessonsMapNoon["第6节课"] = item.lessonBegintime + "~" + tempM
            }
        }

        mMorningSubjectNum = data.amLessonCount
        when(mMorningSubjectNum) {
            0 -> bt_timetable_lesson0_morning.isChecked = true
            1 -> bt_timetable_lesson1_morning.isChecked = true
            2 -> bt_timetable_lesson2_morning.isChecked = true
            3 -> bt_timetable_lesson3_morning.isChecked = true
            4 -> bt_timetable_lesson4_morning.isChecked = true
            5 -> bt_timetable_lesson5_morning.isChecked = true
            6 -> bt_timetable_lesson6_morning.isChecked = true
        }
        mNoonSubjectNum = data.pmLessonCount
        when(mNoonSubjectNum) {
            0 -> bt_timetable_lesson0_noon.isChecked = true
            1 -> bt_timetable_lesson1_noon.isChecked = true
            2 -> bt_timetable_lesson2_noon.isChecked = true
            3 -> bt_timetable_lesson3_noon.isChecked = true
            4 -> bt_timetable_lesson4_noon.isChecked = true
            5 -> bt_timetable_lesson5_noon.isChecked = true
            6 -> bt_timetable_lesson6_noon.isChecked = true
        }
    }

    // 处理提交返回结果(接口)
    override fun settingResult(msg: String) {
        ToastUtils.showMsg(this, msg)
        setResult(Activity.RESULT_OK)
        finish()
    }

    // 获取班级课程列表集合
    override fun getClassSubjectList(dataList: MutableList<ClassChooseSubjectBean>?) {
        classSubjectList.clear()
        if (dataList != null && dataList.size > 0) {
            for (item in dataList) {
                if (item.isSelected) {
                    classSubjectList.add(item.subjectName)
                }
            }
        }
        // 判断是否班级已经设置了课程
        if (classSubjectList.size > 0) {
            tv_timetable_addlesson.text = "编辑"
            tagFlowLayout.adapter = object : TagAdapter<String>(classSubjectList) {
                override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                    val textView = LayoutInflater.from(this@TimeTableSettingActivity).inflate(
                        R.layout.item_flowlayout_textview,
                        parent,
                        false
                    ) as TextView
                    textView.text = classSubjectList[position]
                    return textView
                }
            }
            tagFlowLayout.setVisible(true)
        } else {
            tv_timetable_addlesson.text = "添加"
            tagFlowLayout.setVisible(false)
        }
    }
}