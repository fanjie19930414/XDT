package com.kapok.apps.maple.xdt.notice.activity

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
import android.view.View.OnClickListener
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.activity.CheckHomeWorkTeacherActivity
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkTeacherListSettingAdapter
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListSettingBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.notice.adapter.NoticeListAdapter
import com.kapok.apps.maple.xdt.notice.bean.NoticeListItemBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeListTeacherBean
import com.kapok.apps.maple.xdt.notice.presenter.NoticeTeacherListPresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.NoticeTeacherListView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.CustomHomeListDataDialog
import com.kotlin.baselibrary.custom.CustomLoadMoreView
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_notice_list_teacher.*
import kotlinx.android.synthetic.main.activity_notice_list_teacher.drawerlayout
import kotlinx.android.synthetic.main.drawerlayout_notice_list_teacher.*
import java.util.*

/**
 * 通知列表教师端
 */
@SuppressLint("SetTextI18n")
class NoticeTeacherListActivity : BaseMVPActivity<NoticeTeacherListPresenter>(),
    NoticeTeacherListView {
    // 从哪里传入的btNoticeNoFinish
    private var fromMine: Boolean = false
    // 班级Id
    private var classId: Int = 0
    private var isHeaderTeacher: Boolean = false
    // 设置阅读状态Adapter
    private lateinit var noticeSettingAdapter: HomeWorkTeacherListSettingAdapter
    // 阅读状态选项
    private lateinit var noticeStateList: MutableList<HomeWorkListSettingBean>
    // 班级回执状态Adapter
    private lateinit var noticeReceiptAdapter: HomeWorkTeacherListSettingAdapter
    // 班级回执选项
    private lateinit var noticeReceiptList: MutableList<HomeWorkListSettingBean>

    // 设置日期Adapter
    private lateinit var homeWorkSettingDataAdapter: HomeWorkTeacherListSettingAdapter
    // 日期选项
    private lateinit var homeWorkDataStateList: MutableList<HomeWorkListSettingBean>
    // 设置班级Adapter
    private lateinit var homeWorkSettingClassAdapter: HomeWorkTeacherListSettingAdapter
    // 班级选项
    private lateinit var homeWorkTeacherClassList: MutableList<HomeWorkListSettingBean>


    // 传参默认值
    // 阅读状态(1:已读；2 未读)
    private var readStatus: Int = 0
    // 回执状态 1 部分未完成 2 全员已完成
    private var receiptStatus: Int = 0
    // 当前页
    private var pageNo: Int = 1
    // 每页记录数
    private var pageSize: Int = 10
    // 当前状态 1 未发布 2进行中 3已结束
    private var state: Int = 0
    // 1(家长 未提交/部分未完成)  2(家长 已提交/全员已完成)
    private var submitStatus: Int = 0


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


    // 通知列表集合
    private lateinit var noticeListTeacher: MutableList<NoticeListItemBean>
    // 通知列表Adapter
    private lateinit var noticeListTeacherAdapter: NoticeListAdapter
    // 空页面发布
    private lateinit var tvClassEmptyAdd: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_list_teacher)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = NoticeTeacherListPresenter(this)
        mPresenter.mView = this
        // 获取传入的classId
        classId = intent.getIntExtra("classId", 0)
        isHeaderTeacher = intent.getBooleanExtra("isHeaderTeacher", false)
        if (isHeaderTeacher) {
            tvNoticeState.text = "班级阅读状态"
            tvNoticeReceiptState.text = "班级回执状态"
            noticeReceiptList = arrayListOf()
            noticeReceiptList.add(HomeWorkListSettingBean(false,"部分未回复",""))
            noticeReceiptList.add(HomeWorkListSettingBean(false,"全员已回复",""))
            noticeStateList = arrayListOf()
            noticeStateList.add(HomeWorkListSettingBean(false, "部分未读", ""))
            noticeStateList.add(HomeWorkListSettingBean(false, "全员已读", ""))
        } else {
            tvNoticeState.text = "阅读状态"
            tvNoticeReceiptState.text = "回执状态"
            noticeReceiptList = arrayListOf()
            noticeReceiptList.add(HomeWorkListSettingBean(false,"未回复",""))
            noticeReceiptList.add(HomeWorkListSettingBean(false,"已回复",""))
            noticeStateList = arrayListOf()
            noticeStateList.add(HomeWorkListSettingBean(false, "未读", ""))
            noticeStateList.add(HomeWorkListSettingBean(false, "已读", ""))
        }
        fromMine = intent.getBooleanExtra("from", false)
        if (fromMine) {
            rlNoticeClass.setVisible(true)
        } else {
            rlNoticeClass.setVisible(false)
        }
        // 配置阅读状态Rv
        rvNoticeState.layoutManager = GridLayoutManager(this, 2)
        noticeSettingAdapter = HomeWorkTeacherListSettingAdapter(this, noticeStateList)
        rvNoticeState.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeTeacherListActivity, 12)
                }
            }
        })
        rvNoticeState.adapter = noticeSettingAdapter


        // 配置回复状态
        rvNoticeReceiptState.layoutManager = GridLayoutManager(this,2)
        noticeReceiptAdapter = HomeWorkTeacherListSettingAdapter(this,noticeReceiptList)
        rvNoticeReceiptState.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeTeacherListActivity, 12)
                }
            }
        })
        rvNoticeReceiptState.adapter = noticeReceiptAdapter

        // 配置设置日期Rv
        homeWorkDataStateList = arrayListOf()
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "今天", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "昨天", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "本周", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "上周", ""))
        rvNoticeTime.layoutManager = GridLayoutManager(this, 2)
        homeWorkSettingDataAdapter = HomeWorkTeacherListSettingAdapter(this, homeWorkDataStateList)
        rvNoticeTime.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeTeacherListActivity, 12)
                }
            }
        })
        rvNoticeTime.adapter = homeWorkSettingDataAdapter


        // 配置班级Rv
        homeWorkTeacherClassList = arrayListOf()
        rvNoticeClass.layoutManager = GridLayoutManager(this, 2)
        homeWorkSettingClassAdapter =
            HomeWorkTeacherListSettingAdapter(this, homeWorkTeacherClassList)
        rvNoticeClass.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeTeacherListActivity, 12)
                }
            }
        })
        rvNoticeClass.adapter = homeWorkSettingClassAdapter


        // 配置列表Rv
        noticeListTeacher = arrayListOf()
        rvNoticeListTeacher.layoutManager = LinearLayoutManager(this)
        noticeListTeacherAdapter = NoticeListAdapter(this, noticeListTeacher, true)
        noticeListTeacherAdapter.setLoadMoreView(CustomLoadMoreView())
        noticeListTeacherAdapter.setOnLoadMoreListener(
            { getNoticeListTeacher(false) },
            rvNoticeListTeacher
        )
        rvNoticeListTeacher.adapter = noticeListTeacherAdapter
        rvNoticeListTeacher.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 12),
                resources.getColor(R.color.xdt_background)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty, rvNoticeListTeacher, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "当前还没有通知列表~"
        tvClassEmptyAdd = emptyView.findViewById(R.id.tvClassEmptyAdd)
        tvClassEmptyAdd.text = "发布"
        noticeListTeacherAdapter.emptyView = emptyView

        // 调用老师所在班级接口
        mPresenter.getTeacherInClasses((AppPrefsUtils.getInt("userId")).toString())
    }

    private fun initListener() {
        // finish
        ivNoticeListTeacherBack.setOnClickListener { finish() }
        // 空页面 发布
        tvClassEmptyAdd.setOnClickListener {
            val intent = Intent(this, SendNoticeActivity::class.java)
            startActivity(intent)
        }
        // DrawLayout弹出控制
        ivNoticeListTeacherCondition.setOnClickListener { drawerlayout.openDrawer(Gravity.END) }
        // 阅读状态单选
        noticeSettingAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
                        for (index in noticeStateList.indices) {
                            if (index != position) {
                                noticeStateList[index].isChoose = false
                            }
                        }
                        noticeStateList[position].isChoose = !noticeStateList[position].isChoose
                        noticeSettingAdapter.notifyDataSetChanged()
                    }
                }
                when (position) {
                    // 进行中 已结束
                    0 -> {
                        readStatus = if (readStatus == "1".toInt()) {
                            0
                        } else {
                            "1".toInt()
                        }
                    }
                    1 -> {
                        readStatus = if (readStatus == "2".toInt()) {
                            0
                        } else {
                            "2".toInt()
                        }
                    }
                }
            }
        // 班级回执状态
        noticeReceiptAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
                        for (index in noticeReceiptList.indices) {
                            if (index != position) {
                                noticeReceiptList[index].isChoose = false
                            }
                        }
                        noticeReceiptList[position].isChoose = !noticeReceiptList[position].isChoose
                        noticeReceiptAdapter.notifyDataSetChanged()
                    }
                }
                when (position) {
                    // 进行中 已结束
                    0 -> {
                        receiptStatus = if (receiptStatus == "1".toInt()) {
                            0
                        } else {
                            "1".toInt()
                        }
                        submitStatus = if (submitStatus == "1".toInt()) {
                            0
                        } else {
                            "1".toInt()
                        }
                    }
                    1 -> {
                        receiptStatus = if (receiptStatus == "2".toInt()) {
                            0
                        } else {
                            "2".toInt()
                        }
                        submitStatus = if (submitStatus == "2".toInt()) {
                            0
                        } else {
                            "2".toInt()
                        }
                    }
                }
            }
        // 班级单选
        homeWorkSettingClassAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvHomeWorkSetting -> {
//                        classId = homeWorkTeacherClassList[position].nameId.toInt()
//                        clearChooseState(homeWorkTeacherClassList)
//                        homeWorkTeacherClassList[position].isChoose = true
//                        homeWorkSettingClassAdapter.notifyDataSetChanged()
//                        clearSelectTime()
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
//                        clearSelectTime()
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
                        if (tvNoticeTimeBegin.text.contains(today) && tvNoticeTimeEnd.text.isEmpty()) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvNoticeTimeBegin.text = ""
                            tvNoticeTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(today)) {
                                1 -> {
                                    tvNoticeTimeBegin.text = "$today 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBegin.text = "$today 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBegin.text = "$today 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBegin.text = "$today 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBegin.text = "$today 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBegin.text = "$today 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBegin.text = "$today 星期日"
                                }
                            }
                            pubStartTime = today
                            pubEndTime = ""
                            tvNoticeTimeEnd.text = ""
                        }
                    }
                    1 -> {
                        val yesterday = DateUtils.getLastDay()
                        if (tvNoticeTimeBegin.text.contains(yesterday) && tvNoticeTimeEnd.text.isEmpty()) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvNoticeTimeBegin.text = ""
                            tvNoticeTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(yesterday)) {
                                1 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBegin.text = "$yesterday 星期日"
                                }
                            }
                            pubStartTime = yesterday
                            pubEndTime = ""
                            tvNoticeTimeEnd.text = ""
                        }
                    }
                    2 -> {
                        val currentWeek = DateUtils.getTimeInterval(Date()).split(",")
                        if (pubStartTime == currentWeek[0] && pubEndTime == currentWeek[1]) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvNoticeTimeBegin.text = ""
                            tvNoticeTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(currentWeek[0])) {
                                1 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBegin.text = currentWeek[0] + " 星期日"
                                }
                            }
                            when (DateUtils.getWeekFullFromTime(currentWeek[1])) {
                                1 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeEnd.text = currentWeek[1] + " 星期日"
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
                            tvNoticeTimeBegin.text = ""
                            tvNoticeTimeEnd.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(lastWeek[0])) {
                                1 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBegin.text = lastWeek[0] + " 星期日"
                                }
                            }
                            when (DateUtils.getWeekFullFromTime(lastWeek[1])) {
                                1 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeEnd.text = lastWeek[1] + " 星期日"
                                }
                            }
                            pubStartTime = lastWeek[0]
                            pubEndTime = lastWeek[1]
                        }
                    }
                }
            }
        // 开始时间
        tvNoticeTimeBegin.setOnClickListener {
            var beginData: String
            customDataDialog =
                CustomHomeListDataDialog(this, R.style.BottomDialog)
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
                            tvNoticeTimeBegin.text = "$beginData 星期一"
                        }
                        2 -> {
                            tvNoticeTimeBegin.text = "$beginData 星期二"
                        }
                        3 -> {
                            tvNoticeTimeBegin.text = "$beginData 星期三"
                        }
                        4 -> {
                            tvNoticeTimeBegin.text = "$beginData 星期四"
                        }
                        5 -> {
                            tvNoticeTimeBegin.text = "$beginData 星期五"
                        }
                        6 -> {
                            tvNoticeTimeBegin.text = "$beginData 星期六"
                        }
                        7 -> {
                            tvNoticeTimeBegin.text = "$beginData 星期日"
                        }
                    }
                    pubStartTime = beginData
                }
            })
            customDataDialog.setOnDismissListener { drawerlayout.openDrawer(Gravity.END) }
        }
        // 结束时间
        tvNoticeTimeEnd.setOnClickListener {
            var endData: String
            customDataDialog =
                CustomHomeListDataDialog(this, R.style.BottomDialog)
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
                            tvNoticeTimeEnd.text = "$endData 星期一"
                        }
                        2 -> {
                            tvNoticeTimeEnd.text = "$endData 星期二"
                        }
                        3 -> {
                            tvNoticeTimeEnd.text = "$endData 星期三"
                        }
                        4 -> {
                            tvNoticeTimeEnd.text = "$endData 星期四"
                        }
                        5 -> {
                            tvNoticeTimeEnd.text = "$endData 星期五"
                        }
                        6 -> {
                            tvNoticeTimeEnd.text = "$endData 星期六"
                        }
                        7 -> {
                            tvNoticeTimeEnd.text = "$endData 星期日"
                        }
                    }
                    pubEndTime = endData
                }
            })
            customDataDialog.setOnDismissListener { drawerlayout.openDrawer(Gravity.END) }
        }
        // 列表点击事件
        noticeListTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(
                    this,
                    CheckNoticeTeacherActivity::class.java
                )
                intent.putExtra("noticeItemInfo", noticeListTeacher[position])
                startActivity(intent)
            }
        // 重置
        btNoticeClear.setOnClickListener {
            // 作业状态
            for (item in noticeStateList) {
                item.isChoose = false
            }
            noticeSettingAdapter.notifyDataSetChanged()
            state = 0
            // 班级完成情况
            for (item in noticeReceiptList) {
                item.isChoose = false
            }
            noticeReceiptAdapter.notifyDataSetChanged()
            submitStatus = 0
            // 发布开始时间/结束时间
            pubEndTime = ""
            pubStartTime = ""
            tvNoticeTimeBegin.text = ""
            tvNoticeTimeEnd.text = ""
            for (item in homeWorkDataStateList) {
                item.isChoose = false
            }
            clearSelectTime()
            homeWorkSettingDataAdapter.notifyDataSetChanged()
        }
        // 确定
        btNoticeConfirm.setOnClickListener {
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
                    getNoticeListTeacher(true)
                    drawerlayout.closeDrawer(Gravity.END)
                } else {
                    ToastUtils.showMsg(this, "结束日期应大于开始日期")
                }
            } else {
                pageNo = 1
                pageSize = 10
                // 调用列表接口
                getNoticeListTeacher(true)
                drawerlayout.closeDrawer(Gravity.END)
            }
        }
        // 快速发布通知
        FabSendNotice.setOnClickListener {
            // 发通知
            val intent = Intent(this@NoticeTeacherListActivity, SendNoticeActivity::class.java)
            startActivity(intent)
        }
    }

    // 清空选中状态
    private fun clearChooseState(dataList: MutableList<HomeWorkListSettingBean>) {
        for (item in dataList) {
            item.isChoose = false
        }
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

    override fun onResume() {
        super.onResume()
        // 调用列表接口
        getNoticeListTeacher(true)
    }

    private fun getNoticeListTeacher(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getNoticeListTeacher(
            classId,
            2,
            true,
            pageNo,
            pageSize,
            pubEndTime,
            pubStartTime,
            readStatus,
            receiptStatus,
            state,
            receiptStatus,
            AppPrefsUtils.getInt("userId"),
            isFirst
        )
    }

    // 老师通知列表端回调
    override fun getNoticeTeacherListBean(bean: NoticeListTeacherBean, isFirst: Boolean) {
        // 列表
        if (isFirst) {
            noticeListTeacher.clear()
            noticeListTeacherAdapter.setNewData(bean.list)
            rvNoticeListTeacher.scrollToPosition(0)
            if (bean.list != null && bean.list.isNotEmpty()) {
                for (item in bean.list) {
                    noticeListTeacher.add(item)
                }
            }
        } else {
            if (bean.list != null && bean.list.isNotEmpty()) {
                noticeListTeacherAdapter.addData(bean.list)
                for (item in bean.list) {
                    noticeListTeacher.add(item)
                }
            }
        }
        noticeListTeacherAdapter.setEnableLoadMore(noticeListTeacherAdapter.itemCount - 1 < bean.total)
        if (noticeListTeacherAdapter.itemCount - 1 < bean.total) {
            noticeListTeacherAdapter.loadMoreComplete()
        } else {
            noticeListTeacherAdapter.loadMoreEnd()
        }
    }

    // 获取老师所在班级回调
    override fun getTeacherInClasses(list: MutableList<TeacherInClasses>?) {
        homeWorkTeacherClassList.clear()
        if (list != null && list.size > 0) {
            if (fromMine) {
                rlNoticeClass.setVisible(true)
            } else {
                rlNoticeClass.setVisible(false)
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
            rlNoticeClass.setVisible(false)
        }
        homeWorkSettingClassAdapter.notifyDataSetChanged()
    }
}