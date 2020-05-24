package com.kapok.apps.maple.xdt.homework.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkListAdapter
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkTeacherListSettingAdapter
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListSettingBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListItemBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.presenter.HomeWorkTeacherListPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkTeacherListView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.*
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_homework_list_teacher.*
import kotlinx.android.synthetic.main.drawerlayout_homework_list_teacher.*
import java.util.*


/**
 * 班级作业列表 老师端
 */
@SuppressLint("SetTextI18n")
class HomeWorkTeacherListActivity : BaseMVPActivity<HomeWorkTeacherListPresenter>(),
    HomeWorkTeacherListView {
    // 从哪里传入的
    private var fromMine: Boolean = false
    // 班级Id
    private var classId: Int = 0
    private var isHeaderTeacher: Boolean = false
    // 设置作业状态Adapter
    private lateinit var homeWorkSettingAdapter: HomeWorkTeacherListSettingAdapter
    // 作业状态选项
    private lateinit var homeWorkStateList: MutableList<HomeWorkListSettingBean>

    // 设置完成情况状态Adapter
    private lateinit var homeWorkFinishAdapter: HomeWorkTeacherListSettingAdapter
    private lateinit var homeWorkFinishList: MutableList<HomeWorkListSettingBean>

    // 设置作业科目Adapter
    private lateinit var homeWorkSubjectAdapter: HomeWorkTeacherListSettingAdapter
    private lateinit var homeWorkSubjectList: MutableList<HomeWorkListSettingBean>

    // 设置日期Adapter
    private lateinit var homeWorkSettingDataAdapter: HomeWorkTeacherListSettingAdapter
    // 日期选项
    private lateinit var homeWorkDataStateList: MutableList<HomeWorkListSettingBean>
    // 设置班级Adapter
    private lateinit var homeWorkSettingClassAdapter: HomeWorkTeacherListSettingAdapter
    // 班级选项
    private lateinit var homeWorkTeacherClassList: MutableList<HomeWorkListSettingBean>
    // 传参默认值
    // 作业状态（1 进行中；2 已结束）)
    private var state: String = ""
    // 班级完成情况（1:部分未完成，2:全员已完成）
    private var submitStatus: String = ""
    // 仅查看我的科目
    private var onlySelfWork: Boolean = false
    // 当前页
    private var pageNo: Int = 1
    // 每页记录数
    private var pageSize: Int = 10
    // 发布结束时间/开始时间
    private lateinit var customDataDialog: CustomHomeListDataDialog
    private var pubEndTime: String = ""
    private var pubStartTime: String = ""
    private var hasSelectStart = false
    private var mSelectYearStart = ""
    private var mSelectMonthStart = ""
    private var mSelectDayStart = ""
    private var hasSelectEnd = false
    private var mSelectYearEnd = ""
    private var mSelectMonthEnd = ""
    private var mSelectDayEnd = ""
    // 作业列表集合
    private lateinit var homeWorkListTeacher: MutableList<HomeWorkListItemBean>
    // 作业列表Adapter
    private lateinit var homeWorkListTeacherAdapter: HomeWorkListAdapter
    // 空页面发布
    private lateinit var tvClassEmptyAdd: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_list_teacher)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = HomeWorkTeacherListPresenter(this)
        mPresenter.mView = this
        // 获取传入的classId
        classId = intent.getIntExtra("classId", 0)
        isHeaderTeacher = intent.getBooleanExtra("isHeaderTeacher", false)
        fromMine = intent.getBooleanExtra("from", false)
        if (fromMine) {
            rlHomeWorkClass.setVisible(true)
        } else {
            rlHomeWorkClass.setVisible(false)
        }
        if (isHeaderTeacher) {
            rlMyHomeWorkSubject.setVisible(true)
        } else {
            rlMyHomeWorkSubject.setVisible(false)
        }
        // 配置设置作业状态Rv
        homeWorkStateList = arrayListOf()
        homeWorkStateList.add(HomeWorkListSettingBean(false, "进行中", ""))
        homeWorkStateList.add(HomeWorkListSettingBean(false, "已结束", ""))
        rvHomeWorkState.layoutManager = GridLayoutManager(this, 2)
        homeWorkSettingAdapter = HomeWorkTeacherListSettingAdapter(this, homeWorkStateList)
        rvHomeWorkState.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = 0
                } else {
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkTeacherListActivity, 12)
                }
            }
        })
        rvHomeWorkState.adapter = homeWorkSettingAdapter
        // 配置班级完成情况
        homeWorkFinishList = arrayListOf()
        homeWorkFinishList.add(HomeWorkListSettingBean(false, "部分未完成", ""))
        homeWorkFinishList.add(HomeWorkListSettingBean(false, "全部已完成", ""))
        rvHomeWorkFinishState.layoutManager = GridLayoutManager(this, 2)
        homeWorkFinishAdapter = HomeWorkTeacherListSettingAdapter(this, homeWorkFinishList)
        rvHomeWorkFinishState.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = 0
                } else {
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkTeacherListActivity, 12)
                }
            }
        })
        rvHomeWorkFinishState.adapter = homeWorkFinishAdapter
        // 配置作业科目
        homeWorkSubjectList = arrayListOf()
        homeWorkSubjectList.add(HomeWorkListSettingBean(false, "仅看我的科目", ""))
        rvHomeWorkSubject.layoutManager = GridLayoutManager(this, 2)
        homeWorkSubjectAdapter = HomeWorkTeacherListSettingAdapter(this, homeWorkSubjectList)
        rvHomeWorkSubject.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = 0
                } else {
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkTeacherListActivity, 12)
                }
            }
        })
        rvHomeWorkSubject.adapter = homeWorkSubjectAdapter
        // 配置设置日期Rv
        homeWorkDataStateList = arrayListOf()
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "今天", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "昨天", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "本周", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "上周", ""))
        rvHomeWorkTime.layoutManager = GridLayoutManager(this, 2)
        homeWorkSettingDataAdapter = HomeWorkTeacherListSettingAdapter(this, homeWorkDataStateList)
        rvHomeWorkTime.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = 0
                } else {
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkTeacherListActivity, 12)
                }
            }
        })
        rvHomeWorkTime.adapter = homeWorkSettingDataAdapter
        // 配置班级Rv
        homeWorkTeacherClassList = arrayListOf()
        rvHomeWorkClass.layoutManager = GridLayoutManager(this, 2)
        homeWorkSettingClassAdapter =
            HomeWorkTeacherListSettingAdapter(this, homeWorkTeacherClassList)
        rvHomeWorkClass.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = 0
                } else {
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkTeacherListActivity, 12)
                }
            }
        })
        rvHomeWorkClass.adapter = homeWorkSettingClassAdapter
        // 调用老师所在班级接口
        mPresenter.getTeacherInClasses((AppPrefsUtils.getInt("userId")).toString())
        // 配置列表Rv
        homeWorkListTeacher = arrayListOf()
        rvHomeWorkListTeacher.layoutManager = LinearLayoutManager(this)
        homeWorkListTeacherAdapter = HomeWorkListAdapter(this, homeWorkListTeacher,true)
        homeWorkListTeacherAdapter.setLoadMoreView(CustomLoadMoreView())
        homeWorkListTeacherAdapter.setOnLoadMoreListener(
            { getHomeWorkListTeacher(false) },
            rvHomeWorkListTeacher
        )
        rvHomeWorkListTeacher.adapter = homeWorkListTeacherAdapter
        rvHomeWorkListTeacher.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 12),
                resources.getColor(R.color.xdt_background)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty, rvHomeWorkListTeacher, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "当前还没有作业列表~"
        tvClassEmptyAdd = emptyView.findViewById(R.id.tvClassEmptyAdd)
        tvClassEmptyAdd.text = "发布"
        homeWorkListTeacherAdapter.emptyView = emptyView
    }

    override fun onResume() {
        super.onResume()
        // 调用列表接口
        getHomeWorkListTeacher(true)
    }

    private fun initListener() {
        ivHomeWorkListTeacherBack.setOnClickListener { finish() }
        // 空页面 发布
        tvClassEmptyAdd.setOnClickListener {
            val intent = Intent(this, SendHomeWorkActivity::class.java)
            intent.putExtra("fromCheckHomeWork", false)
            startActivity(intent)
        }
        // DrawLayout弹出控制
        ivHomeWorkListTeacherCondition.setOnClickListener { drawerlayout.openDrawer(Gravity.END) }
        // 作业状态单选
        homeWorkSettingAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
                        for (index in homeWorkStateList.indices) {
                            if (index != position) {
                                homeWorkStateList[index].isChoose = false
                            }
                        }
                        homeWorkStateList[position].isChoose = !homeWorkStateList[position].isChoose
                        homeWorkSettingAdapter.notifyDataSetChanged()
                    }
                }
                when (position) {
                    // 进行中 已结束
                    0 -> {
                        state = if (state == "1") {
                            ""
                        } else {
                            "1"
                        }
                    }
                    1 -> {
                        state = if (state == "2") {
                            ""
                        } else {
                            "2"
                        }
                    }
                }
            }
        // 班级完成情况筛选
        homeWorkFinishAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
                        for (index in homeWorkFinishList.indices) {
                            if (index != position) {
                                homeWorkFinishList[index].isChoose = false
                            }
                        }
                        homeWorkFinishList[position].isChoose = !homeWorkFinishList[position].isChoose
                        homeWorkFinishAdapter.notifyDataSetChanged()
                    }
                }
                when (position) {
                    // 进行中 已结束
                    0 -> {
                        submitStatus = if (submitStatus == "1") {
                            ""
                        } else {
                            "1"
                        }
                    }
                    1 -> {
                        submitStatus = if (submitStatus == "2") {
                            ""
                        } else {
                            "2"
                        }
                    }
                }
            }
        // 作业科目筛选
        homeWorkSubjectAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
                        homeWorkSubjectList[position].isChoose = !homeWorkSubjectList[position].isChoose
                        onlySelfWork = !onlySelfWork
                        homeWorkSubjectAdapter.notifyDataSetChanged()
                    }
                }
            }
        // 班级单选
        homeWorkSettingClassAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
//                        clearChooseState(homeWorkTeacherClassList)
//                        homeWorkTeacherClassList[position].isChoose = true
//                        homeWorkSettingClassAdapter.notifyDataSetChanged()
                        for (index in homeWorkTeacherClassList.indices) {
                            if (index != position) {
                                homeWorkTeacherClassList[index].isChoose = false
                            }
                        }
                        homeWorkTeacherClassList[position].isChoose = !homeWorkTeacherClassList[position].isChoose
                        classId = if (classId == 0) {
                            homeWorkTeacherClassList[position].nameId.toInt()
                        } else {
                            0
                        }
                        homeWorkSettingClassAdapter.notifyDataSetChanged()
                        clearSelectTime()
                    }
                }
            }
        // 时间单选
        homeWorkSettingDataAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
//                        clearChooseState(homeWorkDataStateList)
//                        homeWorkDataStateList[position].isChoose = true
//                        homeWorkSettingDataAdapter.notifyDataSetChanged()
                        for (index in homeWorkDataStateList.indices) {
                            if (index != position) {
                                homeWorkDataStateList[index].isChoose = false
                            }
                        }
                        homeWorkDataStateList[position].isChoose = !homeWorkDataStateList[position].isChoose
                        homeWorkSettingDataAdapter.notifyDataSetChanged()
                        clearSelectTime()
                    }
                }
                when (position) {
                    // 今天 昨天 本周 上周
                    0 -> {
                        val currentMonth: String = if ((DateUtils.getMonth()) > 9) {
                            "${DateUtils.getMonth()}"
                        } else {
                            "0${DateUtils.getMonth()}"
                        }
                        val currentDay: String = if ((DateUtils.getDay()) > 9) {
                            "${DateUtils.getDay()}"
                        } else {
                            "0${DateUtils.getDay()}"
                        }
                        val today = DateUtils.getYear().toString() + "-" + currentMonth + "-" + currentDay
                        if (tvHomeWorkTimeBegin.text.contains(today) && tvHomeWorkTimeEnd.text.isEmpty()) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvHomeWorkTimeBegin.text = ""
                            tvHomeWorkTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(today)) {
                                1 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期一"
                                }
                                2 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期二"
                                }
                                3 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期三"
                                }
                                4 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期四"
                                }
                                5 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期五"
                                }
                                6 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期六"
                                }
                                7 -> {
                                    tvHomeWorkTimeBegin.text = "$today 星期日"
                                }
                            }
                            pubStartTime = today
                            pubEndTime = ""
                            tvHomeWorkTimeEnd.text = ""
                        }
                    }
                    1 -> {
                        val yesterday = DateUtils.getLastDay()
                        if (tvHomeWorkTimeBegin.text.contains(yesterday) && tvHomeWorkTimeEnd.text.isEmpty()) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvHomeWorkTimeBegin.text = ""
                            tvHomeWorkTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(yesterday)) {
                                1 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期一"
                                }
                                2 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期二"
                                }
                                3 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期三"
                                }
                                4 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期四"
                                }
                                5 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期五"
                                }
                                6 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期六"
                                }
                                7 -> {
                                    tvHomeWorkTimeBegin.text = "$yesterday 星期日"
                                }
                            }
                            pubStartTime = yesterday
                            pubEndTime = ""
                            tvHomeWorkTimeEnd.text = ""
                        }
                    }
                    2 -> {
                        val currentWeek = DateUtils.getTimeInterval(Date()).split(",")
                        if (pubStartTime == currentWeek[0] && pubEndTime == currentWeek[1]) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvHomeWorkTimeBegin.text = ""
                            tvHomeWorkTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(currentWeek[0])) {
                                1 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期一"
                                }
                                2 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期二"
                                }
                                3 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期三"
                                }
                                4 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期四"
                                }
                                5 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期五"
                                }
                                6 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期六"
                                }
                                7 -> {
                                    tvHomeWorkTimeBegin.text = currentWeek[0] + " 星期日"
                                }
                            }
                            when (DateUtils.getWeekFullFromTime(currentWeek[1])) {
                                1 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期一"
                                }
                                2 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期二"
                                }
                                3 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期三"
                                }
                                4 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期四"
                                }
                                5 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期五"
                                }
                                6 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期六"
                                }
                                7 -> {
                                    tvHomeWorkTimeEnd.text = currentWeek[1] + " 星期日"
                                }
                            }
                            pubStartTime = currentWeek[0]
                            pubEndTime = currentWeek[1]
                        }
                    }
                    3 -> {
                        val lastWeek = DateUtils.getLastTimeInterval().split(",")
                        if (pubStartTime == lastWeek[0] && pubEndTime == lastWeek[1]) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvHomeWorkTimeBegin.text = ""
                            tvHomeWorkTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(lastWeek[0])) {
                                1 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期一"
                                }
                                2 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期二"
                                }
                                3 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期三"
                                }
                                4 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期四"
                                }
                                5 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期五"
                                }
                                6 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期六"
                                }
                                7 -> {
                                    tvHomeWorkTimeBegin.text = lastWeek[0] + " 星期日"
                                }
                            }
                            when (DateUtils.getWeekFullFromTime(lastWeek[1])) {
                                1 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期一"
                                }
                                2 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期二"
                                }
                                3 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期三"
                                }
                                4 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期四"
                                }
                                5 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期五"
                                }
                                6 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期六"
                                }
                                7 -> {
                                    tvHomeWorkTimeEnd.text = lastWeek[1] + " 星期日"
                                }
                            }
                            pubStartTime = lastWeek[0]
                            pubEndTime = lastWeek[1]
                        }
                    }
                }
            }
        // 开始时间
        tvHomeWorkTimeBegin.setOnClickListener {
            var beginData: String
            customDataDialog =
                CustomHomeListDataDialog(this@HomeWorkTeacherListActivity, R.style.BottomDialog)
            customDataDialog.setTitle("请选择开始时间")
            if (hasSelectStart) {
                customDataDialog.setSelectedTime(
                    mSelectYearStart,
                    mSelectMonthStart,
                    mSelectDayStart
                )
            } else {
                customDataDialog.getCurrentTime()
            }
            drawerlayout.closeDrawer(Gravity.END)
            customDataDialog.show()
            customDataDialog.setOnselectDataListener(object :
                CustomHomeListDataDialog.SelectDataListener {
                override fun selectData(year: String, month: String, day: String) {
                    hasSelectStart = true
                    mSelectYearStart = year
                    mSelectMonthStart =
                        (if (Integer.valueOf(month) > 9) month else "0$month").toString()
                    mSelectDayStart = (if (Integer.valueOf(day) > 9) day else "0$day").toString()
                    beginData = "$mSelectYearStart-$mSelectMonthStart-$mSelectDayStart"
                    for (item in homeWorkDataStateList) {
                        item.isChoose = false
                    }
                    homeWorkSettingDataAdapter.notifyDataSetChanged()
                    when (DateUtils.getWeekFullFromTime(beginData)) {
                        1 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期一"
                        }
                        2 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期二"
                        }
                        3 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期三"
                        }
                        4 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期四"
                        }
                        5 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期五"
                        }
                        6 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期六"
                        }
                        7 -> {
                            tvHomeWorkTimeBegin.text = "$beginData 星期日"
                        }
                    }
                    pubStartTime = beginData
                }
            })
            customDataDialog.setOnDismissListener { drawerlayout.openDrawer(Gravity.END) }
        }
        // 结束时间
        tvHomeWorkTimeEnd.setOnClickListener {
            var endData: String
            customDataDialog =
                CustomHomeListDataDialog(this@HomeWorkTeacherListActivity, R.style.BottomDialog)
            customDataDialog.setTitle("请选择结束时间")
            if (hasSelectEnd) {
                customDataDialog.setSelectedTime(
                    mSelectYearEnd,
                    mSelectMonthEnd,
                    mSelectDayEnd
                )
            } else {
                customDataDialog.getCurrentTime()
            }
            drawerlayout.closeDrawer(Gravity.END)
            customDataDialog.show()
            customDataDialog.setOnselectDataListener(object :
                CustomHomeListDataDialog.SelectDataListener {
                override fun selectData(year: String, month: String, day: String) {
                    hasSelectEnd = true
                    mSelectYearEnd = year
                    mSelectMonthEnd =
                        (if (Integer.valueOf(month) > 9) month else "0$month").toString()
                    mSelectDayEnd = (if (Integer.valueOf(day) > 9) day else "0$day").toString()
                    endData = "$mSelectYearEnd-$mSelectMonthEnd-$mSelectDayEnd"
                    for (item in homeWorkDataStateList) {
                        item.isChoose = false
                    }
                    homeWorkSettingDataAdapter.notifyDataSetChanged()
                    when (DateUtils.getWeekFullFromTime(endData)) {
                        1 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期一"
                        }
                        2 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期二"
                        }
                        3 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期三"
                        }
                        4 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期四"
                        }
                        5 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期五"
                        }
                        6 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期六"
                        }
                        7 -> {
                            tvHomeWorkTimeEnd.text = "$endData 星期日"
                        }
                    }
                    pubEndTime = endData
                }
            })
            customDataDialog.setOnDismissListener { drawerlayout.openDrawer(Gravity.END) }
        }
        // 列表点击事件
        homeWorkListTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(
                    this@HomeWorkTeacherListActivity,
                    CheckHomeWorkTeacherActivity::class.java
                )
                intent.putExtra("homeWorkItemInfo", homeWorkListTeacher[position])
                startActivity(intent)
            }
        // 重置
        btHomeWorkClear.setOnClickListener {
            // 作业状态
            for (item in homeWorkStateList) {
                item.isChoose = false
            }
            for (item in homeWorkFinishList) {
                item.isChoose = false
            }
            for (item in homeWorkSubjectList) {
                item.isChoose = false
            }
            homeWorkSettingAdapter.notifyDataSetChanged()
            state = ""
            // 班级完成情况
            homeWorkFinishAdapter.notifyDataSetChanged()
            submitStatus = ""
            // 作业科目
            homeWorkSubjectAdapter.notifyDataSetChanged()
            onlySelfWork = false
            // 发布开始时间/结束时间
            pubEndTime = ""
            pubStartTime = ""
            tvHomeWorkTimeBegin.text = ""
            tvHomeWorkTimeEnd.text = ""
            for (item in homeWorkDataStateList) {
                item.isChoose = false
            }
            clearSelectTime()
            homeWorkSettingDataAdapter.notifyDataSetChanged()
        }
        // 确定
        btHomeWorkConfirm.setOnClickListener {
            // 判断时间
            if (pubStartTime.isNotEmpty() && pubEndTime.isNotEmpty()) {
                if (DateUtils.stringToLong(pubEndTime, "yyyy-MM-dd") - DateUtils.stringToLong(
                        pubStartTime,
                        "yyyy-MM-dd"
                    ) > 0
                ) {
                    pageNo = 1
                    pageSize = 10
                    // 调用列表接口
                    getHomeWorkListTeacher(true)
                    drawerlayout.closeDrawer(Gravity.END)
                } else {
                    ToastUtils.showMsg(this, "结束日期应大于开始日期")
                }
            } else {
                pageNo = 1
                pageSize = 10
                // 调用列表接口
                getHomeWorkListTeacher(true)
                drawerlayout.closeDrawer(Gravity.END)
            }
        }
        // 快速发布作业入口
        FabSendHomeWork.setOnClickListener {
            // 发作业
            val intent = Intent(this@HomeWorkTeacherListActivity, SendHomeWorkActivity::class.java)
            intent.putExtra("fromCheckHomeWork", false)
            startActivity(intent)
        }
    }

    private fun getHomeWorkListTeacher(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getHomeWorkListTeacher(
            classId,
            onlySelfWork,
            pageNo,
            pageSize,
            pubEndTime,
            pubStartTime,
            state,
            submitStatus,
            AppPrefsUtils.getInt("userId").toString(),
            isFirst
        )
    }

    // 清空时间选中状态
    private fun clearSelectTime() {
        hasSelectStart = false
        mSelectYearStart = ""
        mSelectMonthStart = ""
        mSelectDayStart = ""
        hasSelectEnd = false
        mSelectYearEnd = ""
        mSelectMonthEnd = ""
        mSelectDayEnd = ""
    }

    // 清空选中状态
    private fun clearChooseState(dataList: MutableList<HomeWorkListSettingBean>) {
        for (item in dataList) {
            item.isChoose = false
        }
    }

    // 老师作业列表端回调
    override fun getHomeWorkListTeacher(bean: HomeWorkListTeacherBean, isFirst: Boolean) {
        // 列表
        if (isFirst) {
            homeWorkListTeacher.clear()
            homeWorkListTeacherAdapter.setNewData(bean.list)
            rvHomeWorkListTeacher.scrollToPosition(0)
            if (bean.list != null && bean.list.isNotEmpty()) {
                for (item in bean.list) {
                    homeWorkListTeacher.add(item)
                }
            }
        } else {
            if (bean.list != null && bean.list.isNotEmpty()) {
                homeWorkListTeacherAdapter.addData(bean.list)
                for (item in bean.list) {
                    homeWorkListTeacher.add(item)
                }
            }
        }
        homeWorkListTeacherAdapter.setEnableLoadMore(homeWorkListTeacherAdapter.itemCount - 1 < bean.total)
        if (homeWorkListTeacherAdapter.itemCount - 1 < bean.total) {
            homeWorkListTeacherAdapter.loadMoreComplete()
        } else {
            homeWorkListTeacherAdapter.loadMoreEnd()
        }
    }

    // 获取老师所在班级回调
    override fun getTeacherInClasses(list: MutableList<TeacherInClasses>?) {
        homeWorkTeacherClassList.clear()
        if (list != null && list.size > 0) {
            if (fromMine) {
                rlHomeWorkClass.setVisible(true)
            } else {
                rlHomeWorkClass.setVisible(false)
            }
            for (item in list) {
                homeWorkTeacherClassList.add(
                    HomeWorkListSettingBean(
                        false,
                        item.grade + item.className + "班",
                        item.classId.toString()
                    )
                )
            }
        } else {
            rlHomeWorkClass.setVisible(false)
        }
        homeWorkSettingClassAdapter.notifyDataSetChanged()
    }
}