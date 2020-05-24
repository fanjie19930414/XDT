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
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkListAdapter
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkTeacherListSettingAdapter
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListSettingBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListItemBean
import com.kapok.apps.maple.xdt.homework.presenter.HomeWorkParentListPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkParentListView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.*
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_homework_choossclass.*
import kotlinx.android.synthetic.main.activity_homework_list_teacher.*
import kotlinx.android.synthetic.main.drawerlayout_homework_list_teacher.*
import java.util.*


/**
 * 班级作业列表 家长端(与教师端目前一致 只是调用接口不同)
 */
@SuppressLint("SetTextI18n")
class HomeWorkParentListActivity : BaseMVPActivity<HomeWorkParentListPresenter>(),
    HomeWorkParentListView {
    // 班级Id
    private var classId: Int = 0
    // 设置提交状态Adapter
    private lateinit var homeWorkSettingAdapter: HomeWorkTeacherListSettingAdapter
    // 提交状态选项
    private lateinit var homeWorkStateList: MutableList<HomeWorkListSettingBean>

    // 设置作业状态Adapter
    private lateinit var homeWorkFinishAdapter: HomeWorkTeacherListSettingAdapter
    private lateinit var homeWorkFinishList: MutableList<HomeWorkListSettingBean>

    // 设置日期Adapter
    private lateinit var homeWorkSettingDataAdapter: HomeWorkTeacherListSettingAdapter
    // 日期选项
    private lateinit var homeWorkDataStateList: MutableList<HomeWorkListSettingBean>
    // 传参默认值
    // 提交状态（1:未提交；2 已提交）)
    private var submitStatus: String = ""
    // 作业状态（1:进行中，2:已结束）
    private var state: String = ""
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
    private lateinit var homeWorkListParent: MutableList<HomeWorkListItemBean>
    // 作业列表Adapter
    private lateinit var homeWorkListParentAdapter: HomeWorkListAdapter
    // 空页面
    private lateinit var llClassListEmpty: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_list_teacher)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = HomeWorkParentListPresenter(this)
        mPresenter.mView = this
        // 获取传参
        classId = intent.getIntExtra("classId",0)
        // 隐藏仅看我的科目
        rlMyHomeWorkSubject.setVisible(false)
        // 配置提交状态Rv
        tvHomeWorkState.text = "提交状态"
        homeWorkStateList = arrayListOf()
        homeWorkStateList.add(HomeWorkListSettingBean(false, "未提交",""))
        homeWorkStateList.add(HomeWorkListSettingBean(false, "已提交",""))
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
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkParentListActivity, 12)
                }
            }
        })
        rvHomeWorkState.adapter = homeWorkSettingAdapter
        // 配置作业状态
        tvHomeWorkFinishState.text = "作业状态"
        homeWorkFinishList = arrayListOf()
        homeWorkFinishList.add(HomeWorkListSettingBean(false, "进行中", ""))
        homeWorkFinishList.add(HomeWorkListSettingBean(false, "已结束", ""))
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
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkParentListActivity, 12)
                }
            }
        })
        rvHomeWorkFinishState.adapter = homeWorkFinishAdapter
        // 配置设置日期Rv
        homeWorkDataStateList = arrayListOf()
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "今天",""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "昨天",""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "本周",""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "上周",""))
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
                    outRect.left = Dp2pxUtils.dp2px(this@HomeWorkParentListActivity, 12)
                }
            }
        })
        rvHomeWorkTime.adapter = homeWorkSettingDataAdapter
        // 配置列表Rv
        homeWorkListParent = arrayListOf()
        rvHomeWorkListTeacher.layoutManager = LinearLayoutManager(this)
        homeWorkListParentAdapter = HomeWorkListAdapter(this, homeWorkListParent,false)
        homeWorkListParentAdapter.setLoadMoreView(CustomLoadMoreView())
        homeWorkListParentAdapter.setOnLoadMoreListener(
            { getHomeWorkListParent(false) },
            rvHomeWorkListTeacher
        )
        rvHomeWorkListTeacher.adapter = homeWorkListParentAdapter
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
        llClassListEmpty = emptyView.findViewById(R.id.ll_class_list_empty)
        llClassListEmpty.setVisible(false)
        homeWorkListParentAdapter.emptyView = emptyView
    }

    override fun onResume() {
        super.onResume()
        // 调用列表接口
        getHomeWorkListParent(true)
    }

    private fun initListener() {
        // 返回
        ivHomeWorkListTeacherBack.setOnClickListener { finish() }
        // DrawLayout弹出控制
        ivHomeWorkListTeacherCondition.setOnClickListener { drawerlayout.openDrawer(Gravity.END) }
        // 提交状态单选
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
        // 作业状态筛选
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
                CustomHomeListDataDialog(this@HomeWorkParentListActivity, R.style.BottomDialog)
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
                CustomHomeListDataDialog(this@HomeWorkParentListActivity, R.style.BottomDialog)
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
        homeWorkListParentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(
                    this,
                    CheckHomeWorkParentActivity::class.java
                )
                intent.putExtra("homeWorkItemInfo", homeWorkListParent[position])
                intent.putExtra("classId",classId)
                startActivity(intent)
            }
        // 重置
        btHomeWorkClear.setOnClickListener {
            // 提交状态
            for (item in homeWorkStateList) {
                item.isChoose = false
            }
            for (item in homeWorkFinishList) {
                item.isChoose = false
            }
            homeWorkSettingAdapter.notifyDataSetChanged()
            submitStatus = ""
            // 作业状态
            homeWorkFinishAdapter.notifyDataSetChanged()
            state = ""
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
                    getHomeWorkListParent(true)
                    drawerlayout.closeDrawer(Gravity.END)
                } else {
                    ToastUtils.showMsg(this, "结束日期应大于开始日期")
                }
            } else {
                pageNo = 1
                pageSize = 10
                // 调用列表接口
                getHomeWorkListParent(true)
                drawerlayout.closeDrawer(Gravity.END)
            }
        }
    }

    private fun getHomeWorkListParent(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getHomeWorkListParent(
            classId,
            pageNo,
            pageSize,
            AppPrefsUtils.getInt("userId").toString(),
            pubEndTime,
            pubStartTime,
            submitStatus,
            state,
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

    // 家长作业列表端回调
    override fun getHomeWorkListParent(bean: HomeWorkListTeacherBean, isFirst: Boolean) {
        // 列表
        if (isFirst) {
            homeWorkListParent.clear()
            homeWorkListParentAdapter.setNewData(bean.list)
            rvHomeWorkListTeacher.scrollToPosition(0)
            if (bean.list != null && bean.list.isNotEmpty()) {
                for (item in bean.list) {
                    homeWorkListParent.add(item)
                }
            }
        } else {
            if (bean.list != null && bean.list.isNotEmpty()) {
                homeWorkListParentAdapter.addData(bean.list)
                for (item in bean.list) {
                    homeWorkListParent.add(item)
                }
            }
        }
        homeWorkListParentAdapter.setEnableLoadMore(homeWorkListParentAdapter.itemCount - 1 < bean.total)
        if (homeWorkListParentAdapter.itemCount - 1 < bean.total) {
            homeWorkListParentAdapter.loadMoreComplete()
        } else {
            homeWorkListParentAdapter.loadMoreEnd()
        }
    }
}