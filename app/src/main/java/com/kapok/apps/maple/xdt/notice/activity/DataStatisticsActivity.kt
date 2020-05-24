package com.kapok.apps.maple.xdt.notice.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.notice.adapter.NoticeDataStatisticsListAdapter
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsStudentBean
import com.kapok.apps.maple.xdt.notice.presenter.DataStatisticsPresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.DataStatisticsView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_data_statistics.*
import org.w3c.dom.Text

/**
 * 通知数据分析页
 */
class DataStatisticsActivity : BaseMVPActivity<DataStatisticsPresenter>(), DataStatisticsView {
    // 传参
    private var workId = -1
    private var noReceiveNum = -1
    private var haveReceiveNum = -1
    // Rv
    private lateinit var dataList: MutableList<MultiItemEntity>
    private lateinit var noticeAdapter: NoticeDataStatisticsListAdapter
    // emptyView
    private lateinit var emptyView: View
    private lateinit var tvEmptyContent: TextView
    // 回执状态 1 未回执 2已回执
    private var receiptState = 1
    // 是否需要回执
    private var isReceipt: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_statistics)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = DataStatisticsPresenter(this)
        mPresenter.mView = this
        // 获取传参
        workId = intent.getIntExtra("workId",-1)
        noReceiveNum = intent.getIntExtra("noReceiveNum",-1)
        haveReceiveNum = intent.getIntExtra("haveReceiveNum",-1)
        isReceipt = intent.getBooleanExtra("isReceipt",true)
        // Tab
        tabLayoutDataStatistics.tabMode = TabLayout.MODE_FIXED
        if (isReceipt) {
            tabLayoutDataStatistics.addTab(tabLayoutDataStatistics.newTab().setText("未回复（$noReceiveNum）"))
            tabLayoutDataStatistics.addTab(tabLayoutDataStatistics.newTab().setText("已回复（$haveReceiveNum）"))
        } else {
            tabLayoutDataStatistics.addTab(tabLayoutDataStatistics.newTab().setText("未查看（$noReceiveNum）"))
            tabLayoutDataStatistics.addTab(tabLayoutDataStatistics.newTab().setText("已查看（$haveReceiveNum）"))
        }
        // 配置列表Rv
        dataList = arrayListOf()
        noticeAdapter = NoticeDataStatisticsListAdapter(dataList)
        rvNoticeDataStatistics.adapter = noticeAdapter
        rvNoticeDataStatistics.layoutManager = LinearLayoutManager(this)
        rvNoticeDataStatistics.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1),
                resources.getColor(com.kotlin.baselibrary.R.color.login_xdt_view_line)
            )
        )
        // emptyView
        emptyView = LayoutInflater.from(this)
            .inflate(R.layout.layout_class_list_empty2, rvNoticeDataStatistics, false)
        tvEmptyContent = emptyView.findViewById(R.id.tvEmptyContent)
        if (receiptState == 1) {
            tvEmptyContent.text = "所有人都已回复!"
        } else {
            tvEmptyContent.text = "还没有学生回复~"
        }
        noticeAdapter.emptyView = emptyView
        // 调用查看统计接口
        mPresenter.getTeacherNoticeReceiptList(
            receiptState,
            AppPrefsUtils.getInt("userId"),
            workId
        )
    }

    private fun initListener() {
        // finish
        ivDataBackTeacher.setOnClickListener { finish() }
        // tabLayoutDataStatistics
        tabLayoutDataStatistics.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab?> {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        // 未回执
                        receiptState = 1
                        // 调用查看统计接口
                        mPresenter.getTeacherNoticeReceiptList(
                            receiptState,
                            AppPrefsUtils.getInt("userId"),
                            workId
                        )
                        if (receiptState == 1) {
                            tvEmptyContent.text = "所有人都已回复!"
                        } else {
                            tvEmptyContent.text = "还没有学生回复~"
                        }
                        noticeAdapter.emptyView = emptyView
                    }
                    1 -> {
                        // 已回执
                        receiptState = 2
                        // 调用查看统计接口
                        mPresenter.getTeacherNoticeReceiptList(
                            receiptState,
                            AppPrefsUtils.getInt("userId"),
                            workId
                        )
                        if (receiptState == 1) {
                            tvEmptyContent.text = "所有人都已回复!"
                        } else {
                            tvEmptyContent.text = "还没有学生回复~"
                        }
                        noticeAdapter.emptyView = emptyView
                    }
                }
            }
        })
        // 提醒
        noticeAdapter.setOnAdapterClickListener(object : NoticeDataStatisticsListAdapter.AdapterClickListener {
            override fun onAdapterClick(bean: NoticeDataStatisticsStudentBean) {
                val studentIds = arrayListOf<Int>()
                studentIds.add(bean.studentId)
                mPresenter.remindNotice(studentIds,AppPrefsUtils.getInt("userId"),workId)
            }
        })
    }

    // 获取数据分析回执列表页回调
    override fun getTeacherDataStatisticsList(list: MutableList<NoticeDataStatisticsBean>?) {
        dataList.clear()
        if (list != null && list.size > 0) {
            // 判断是否需要回执
            for (item in list) {
                val classInfoBean = NoticeDataStatisticsBean(
                    item.classId,
                    item.className,
                    item.grade,
                    item.gradeId,
                    item.schoolId,
                    item.schoolName,
                    item.startYear,
                    item.studentCount,
                    item.studentDetails,
                    item.submitState
                )
                if (item.studentDetails != null) {
                    for (itemStudent in item.studentDetails) {
                        if (itemStudent.studentAvatar != null) {
                            val studentInfoBean = NoticeDataStatisticsStudentBean(
                                itemStudent.classId,
                                itemStudent.commentStatus,
                                itemStudent.studentAvatar,
                                itemStudent.studentId,
                                itemStudent.studentName
                            )
                            classInfoBean.addSubItem(studentInfoBean)
                        } else {
                            val studentInfoBean = NoticeDataStatisticsStudentBean(
                                itemStudent.classId,
                                itemStudent.commentStatus,
                                null,
                                itemStudent.studentId,
                                itemStudent.studentName
                            )
                            classInfoBean.addSubItem(studentInfoBean)
                        }
                    }
                }
                dataList.add(classInfoBean)
            }
        }
        noticeAdapter.setSubmitState(receiptState)
        // 默认展开第一栏
        noticeAdapter.expand(0)
        noticeAdapter.notifyDataSetChanged()
    }

    override fun remindNotice(msg: String) {
        ToastUtils.showMsg(this,msg)
        // 调用查看统计接口
        mPresenter.getTeacherNoticeReceiptList(
            receiptState,
            AppPrefsUtils.getInt("userId"),
            workId
        )
    }
}