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
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkTeacherListSettingAdapter
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListSettingBean
import com.kapok.apps.maple.xdt.notice.adapter.NoticeListAdapter
import com.kapok.apps.maple.xdt.notice.bean.NoticeListItemBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeListTeacherBean
import com.kapok.apps.maple.xdt.notice.presenter.NoticeParentListPresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.NoticeParentListView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.CustomHomeListDataDialog
import com.kotlin.baselibrary.custom.CustomLoadMoreView
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_notice_list_parent.*
import kotlinx.android.synthetic.main.activity_notice_list_parent.drawerlayout
import kotlinx.android.synthetic.main.drawerlayout_notice_list_parent.*
import java.util.*

/**
 * 通知列表家长端
 */
@SuppressLint("SetTextI18n")
class NoticeParentListActivity : BaseMVPActivity<NoticeParentListPresenter>(),
    NoticeParentListView {
    // 班级Id
    private var classId: Int = 0

    // 设置阅读状态Adapter
    private lateinit var noticeSettingAdapter: HomeWorkTeacherListSettingAdapter
    // 阅读状态选项
    private lateinit var noticeStateList: MutableList<HomeWorkListSettingBean>
    // 回执状态Adapter
    private lateinit var noticeReceiptAdapter: HomeWorkTeacherListSettingAdapter
    // 回执状态选项
    private lateinit var noticeReceiptList: MutableList<HomeWorkListSettingBean>


    // 设置日期Adapter
    private lateinit var homeWorkSettingDataAdapter: HomeWorkTeacherListSettingAdapter
    // 日期选项
    private lateinit var homeWorkDataStateList: MutableList<HomeWorkListSettingBean>


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
    private lateinit var noticeListParent: MutableList<NoticeListItemBean>
    // 通知列表Adapter
    private lateinit var noticeListParentAdapter: NoticeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_list_parent)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = NoticeParentListPresenter(this)
        mPresenter.mView = this
        // 获取传入的classId
        classId = intent.getIntExtra("classId", 0)
        // 配置阅读状态Rv
        noticeStateList = arrayListOf()
        noticeStateList.add(HomeWorkListSettingBean(false, "未读", ""))
        noticeStateList.add(HomeWorkListSettingBean(false, "已读", ""))
        rvNoticeStateParent.layoutManager = GridLayoutManager(this, 2)
        noticeSettingAdapter = HomeWorkTeacherListSettingAdapter(this, noticeStateList)
        rvNoticeStateParent.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeParentListActivity, 12)
                }
            }
        })
        rvNoticeStateParent.adapter = noticeSettingAdapter
        // 配置回执状态Rv
        noticeReceiptList = arrayListOf()
        noticeReceiptList.add(HomeWorkListSettingBean(false,"未回复",""))
        noticeReceiptList.add(HomeWorkListSettingBean(false,"已回复",""))
        rvNoticeReceiptStateParent.layoutManager = GridLayoutManager(this,2)
        noticeReceiptAdapter = HomeWorkTeacherListSettingAdapter(this,noticeReceiptList)
        rvNoticeReceiptStateParent.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeParentListActivity, 12)
                }
            }
        })
        rvNoticeReceiptStateParent.adapter = noticeReceiptAdapter
        // 配置设置日期Rv
        homeWorkDataStateList = arrayListOf()
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "今天", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "昨天", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "本周", ""))
        homeWorkDataStateList.add(HomeWorkListSettingBean(false, "上周", ""))
        rvNoticeTimeParent.layoutManager = GridLayoutManager(this, 2)
        homeWorkSettingDataAdapter = HomeWorkTeacherListSettingAdapter(this, homeWorkDataStateList)
        rvNoticeTimeParent.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.left = Dp2pxUtils.dp2px(this@NoticeParentListActivity, 12)
                }
            }
        })
        rvNoticeTimeParent.adapter = homeWorkSettingDataAdapter


        // 配置列表Rv
        noticeListParent = arrayListOf()
        rvNoticeListParent.layoutManager = LinearLayoutManager(this)
        noticeListParentAdapter = NoticeListAdapter(this, noticeListParent, false)
        noticeListParentAdapter.setLoadMoreView(CustomLoadMoreView())
        noticeListParentAdapter.setOnLoadMoreListener(
            { getNoticeListParent(false) },
            rvNoticeListParent
        )
        rvNoticeListParent.adapter = noticeListParentAdapter
        rvNoticeListParent.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 12),
                resources.getColor(R.color.xdt_background)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty, rvNoticeListParent, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "当前还没有通知列表~"
        noticeListParentAdapter.emptyView = emptyView
    }

    private fun initListener() {
        // finish
        ivNoticeListParentBack.setOnClickListener { finish() }
        // DrawLayout弹出控制
        ivNoticeListParentCondition.setOnClickListener { drawerlayout.openDrawer(Gravity.END) }
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
                        if (tvNoticeTimeBeginParent.text.contains(today) && tvNoticeTimeEndParent.text.isEmpty()) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvNoticeTimeBeginParent.text = ""
                            tvNoticeTimeEndParent.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(today)) {
                                1 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBeginParent.text = "$today 星期日"
                                }
                            }
                            pubStartTime = today
                            pubEndTime = ""
                            tvNoticeTimeEndParent.text = ""
                        }
                    }
                    1 -> {
                        val yesterday = DateUtils.getLastDay()
                        if (tvNoticeTimeBeginParent.text.contains(yesterday) && tvNoticeTimeEndParent.text.isEmpty()) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvNoticeTimeBeginParent.text = ""
                            tvNoticeTimeEndParent.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(yesterday)) {
                                1 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBeginParent.text = "$yesterday 星期日"
                                }
                            }
                            pubStartTime = yesterday
                            pubEndTime = ""
                            tvNoticeTimeEndParent.text = ""
                        }
                    }
                    2 -> {
                        val currentWeek = DateUtils.getTimeInterval(Date()).split(",")
                        if (pubStartTime == currentWeek[0] && pubEndTime == currentWeek[1]) {
                            pubEndTime = ""
                            pubStartTime = ""
                            tvNoticeTimeBeginParent.text = ""
                            tvNoticeTimeEndParent.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(currentWeek[0])) {
                                1 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBeginParent.text = currentWeek[0] + " 星期日"
                                }
                            }
                            when (DateUtils.getWeekFullFromTime(currentWeek[1])) {
                                1 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeEndParent.text = currentWeek[1] + " 星期日"
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
                            tvNoticeTimeBeginParent.text = ""
                            tvNoticeTimeEndParent.text = ""
                        } else {
                            when (DateUtils.getWeekFullFromTime(lastWeek[0])) {
                                1 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeBeginParent.text = lastWeek[0] + " 星期日"
                                }
                            }
                            when (DateUtils.getWeekFullFromTime(lastWeek[1])) {
                                1 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期一"
                                }
                                2 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期二"
                                }
                                3 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期三"
                                }
                                4 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期四"
                                }
                                5 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期五"
                                }
                                6 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期六"
                                }
                                7 -> {
                                    tvNoticeTimeEndParent.text = lastWeek[1] + " 星期日"
                                }
                            }
                            pubStartTime = lastWeek[0]
                            pubEndTime = lastWeek[1]
                        }
                    }
                }
            }
        // 开始时间
        tvNoticeTimeBeginParent.setOnClickListener {
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
                            tvNoticeTimeBeginParent.text = "$beginData 星期一"
                        }
                        2 -> {
                            tvNoticeTimeBeginParent.text = "$beginData 星期二"
                        }
                        3 -> {
                            tvNoticeTimeBeginParent.text = "$beginData 星期三"
                        }
                        4 -> {
                            tvNoticeTimeBeginParent.text = "$beginData 星期四"
                        }
                        5 -> {
                            tvNoticeTimeBeginParent.text = "$beginData 星期五"
                        }
                        6 -> {
                            tvNoticeTimeBeginParent.text = "$beginData 星期六"
                        }
                        7 -> {
                            tvNoticeTimeBeginParent.text = "$beginData 星期日"
                        }
                    }
                    pubStartTime = beginData
                }
            })
            customDataDialog.setOnDismissListener { drawerlayout.openDrawer(Gravity.END) }
        }
        // 结束时间
        tvNoticeTimeEndParent.setOnClickListener {
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
                            tvNoticeTimeEndParent.text = "$endData 星期一"
                        }
                        2 -> {
                            tvNoticeTimeEndParent.text = "$endData 星期二"
                        }
                        3 -> {
                            tvNoticeTimeEndParent.text = "$endData 星期三"
                        }
                        4 -> {
                            tvNoticeTimeEndParent.text = "$endData 星期四"
                        }
                        5 -> {
                            tvNoticeTimeEndParent.text = "$endData 星期五"
                        }
                        6 -> {
                            tvNoticeTimeEndParent.text = "$endData 星期六"
                        }
                        7 -> {
                            tvNoticeTimeEndParent.text = "$endData 星期日"
                        }
                    }
                    pubEndTime = endData
                }
            })
            customDataDialog.setOnDismissListener { drawerlayout.openDrawer(Gravity.END) }
        }
        // 列表点击事件
        noticeListParentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(
                    this,
                    CheckNoticeParentActivity::class.java
                )
                intent.putExtra("noticeItemInfo", noticeListParent[position])
                intent.putExtra("classId",classId)
                startActivity(intent)
            }
        // 重置
        btNoticeClearParent.setOnClickListener {
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
            tvNoticeTimeBeginParent.text = ""
            tvNoticeTimeEndParent.text = ""
            for (item in homeWorkDataStateList) {
                item.isChoose = false
            }
            clearSelectTime()
            homeWorkSettingDataAdapter.notifyDataSetChanged()
        }
        // 确定
        btNoticeConfirmParent.setOnClickListener {
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
                    getNoticeListParent(true)
                    drawerlayout.closeDrawer(Gravity.END)
                } else {
                    ToastUtils.showMsg(this, "结束日期应大于开始日期")
                }
            } else {
                pageNo = 1
                pageSize = 10
                // 调用列表接口
                getNoticeListParent(true)
                drawerlayout.closeDrawer(Gravity.END)
            }
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
        getNoticeListParent(true)
    }

    private fun getNoticeListParent(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getParentNoticeList(
            classId,
            1,
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

    // 家长通知列表端回调
    override fun getNoticeParentListBean(bean: NoticeListTeacherBean, isFirst: Boolean) {
        // 列表
        if (isFirst) {
            noticeListParent.clear()
            noticeListParentAdapter.setNewData(bean.list)
            rvNoticeListParent.scrollToPosition(0)
            if (bean.list != null && bean.list.isNotEmpty()) {
                for (item in bean.list) {
                    noticeListParent.add(item)
                }
            }
        } else {
            if (bean.list != null && bean.list.isNotEmpty()) {
                noticeListParentAdapter.addData(bean.list)
                for (item in bean.list) {
                    noticeListParent.add(item)
                }
            }
        }
        noticeListParentAdapter.setEnableLoadMore(noticeListParentAdapter.itemCount - 1 < bean.total)
        if (noticeListParentAdapter.itemCount - 1 < bean.total) {
            noticeListParentAdapter.loadMoreComplete()
        } else {
            noticeListParentAdapter.loadMoreEnd()
        }
    }
}