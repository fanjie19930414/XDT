package com.kapok.apps.maple.xdt.timetable.activity.timetable_parent

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.adapter.SubjectDetailMorningAdapter
import com.kapok.apps.maple.xdt.timetable.bean.SubjectDetailBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean.TimeTableSubjectBean
import com.kapok.apps.maple.xdt.timetable.presenter.SubjectDetailPresenter
import com.kapok.apps.maple.xdt.timetable.presenter.view.SubjectDetailView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_subject_detail.*
import kotlinx.android.synthetic.main.activity_time_table_parent.*
import kotlinx.android.synthetic.main.activity_time_table_teacher.*
import java.util.*

/**
 *  课程表详情页面（家长）
 *  fanjie
 */
class SubjectDetailActivity : BaseMVPActivity<SubjectDetailPresenter>(), SubjectDetailView {
    // 选中的周几
    private var weekend: Int = -1
    // 选中的第几周
    private lateinit var selectWeekend: String
    // 选中的第几节课
    private var selectNum: Int = -1

    // 上午/下午几节课
    private var morningClassNum: Int = -1
    private var noonClassNum: Int = -1

    // 课程详情上午列表
    private lateinit var mSubjectDetailMorningListBean: MutableList<SubjectDetailBean>
    // 课程详情上午Adapter
    private lateinit var mSubjectDetailMorningAdapter: SubjectDetailMorningAdapter

    // 课程详情下午列表
    private lateinit var mSubjectDetailNoonListBean: MutableList<SubjectDetailBean>
    // 课程详情下午Adapter
    private lateinit var mSubjectDetailNoonAdapter: SubjectDetailMorningAdapter

    private var classId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_detail)
        initView()
        initListener()
    }

    private fun initListener() {
        // 返回
        ivSubjectDetailFinish.setOnClickListener { finish() }
    }

    private fun initView() {
        mPresenter = SubjectDetailPresenter(this)
        mPresenter.mView = this
        initData()
        // 课程数量RecyclerView(上午)
        mSubjectDetailMorningListBean = arrayListOf()
        mSubjectDetailMorningAdapter = SubjectDetailMorningAdapter(mSubjectDetailMorningListBean)
        rvSubjectDetailMorning.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSubjectDetailMorning.adapter = mSubjectDetailMorningAdapter
        // 课程数量RecyclerView(下午)
        mSubjectDetailNoonListBean = arrayListOf()
        mSubjectDetailNoonAdapter = SubjectDetailMorningAdapter(mSubjectDetailNoonListBean)
        rvSubjectDetailNoon.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvSubjectDetailNoon.adapter = mSubjectDetailNoonAdapter
    }

    private fun initData() {
        weekend = intent.getIntExtra("weekend", -1)
        selectWeekend = intent.getStringExtra("selectWeekend")
        selectNum = intent.getIntExtra("selectNum", -1)
        classId = intent.getIntExtra("classId",-1)
        // 获取当前月份
        val currentMonth = DateUtils.getMonth()
        // 获取当前年份
        val currentYear = DateUtils.getYear()
        tvSubjectDetailDate.text = currentYear.toString() + "年" + currentMonth.toString() + "月"
        // 高亮显示点击的是周几
        when (weekend) {
            1 -> {
                llMonday.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv1day.setTextColor(resources.getColor(R.color.common_white))
                tv1dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
            2 -> {
                llTuesDay.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv2day.setTextColor(resources.getColor(R.color.common_white))
                tv2dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
            3 -> {
                llWednesday.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv3day.setTextColor(resources.getColor(R.color.common_white))
                tv3dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
            // 13811484471
            4 -> {
                llThursday.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv4day.setTextColor(resources.getColor(R.color.common_white))
                tv4dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
            5 -> {
                llFriday.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv5day.setTextColor(resources.getColor(R.color.common_white))
                tv5dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
            6 -> {
                llSaturday.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv6day.setTextColor(resources.getColor(R.color.common_white))
                tv6dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
            7 -> {
                llSunday.background = resources.getDrawable(R.drawable.shape_btn_corner8_blue)
                tv7day.setTextColor(resources.getColor(R.color.common_white))
                tv7dayUnit.setTextColor(resources.getColor(R.color.common_white))
            }
        }
        // 调用设置接口
        mPresenter.getSubjectSetting(classId)
    }

    // 获取课程表设置接口(目前没用到)
    override fun getSettingInfo(data: TimeTableSettingInfoBean) {
        if (data.timetableConfigDetailList != null && data.timetableConfigDetailList.size > 0) {
            // 上午几节课
            morningClassNum = data.amLessonCount
            // 下午几节课
            noonClassNum = data.pmLessonCount
            // 获取课程表
            mPresenter.getTimeTableInfo(classId, selectWeekend, false)
        } else {
            ToastUtils.showMsg(this, "该班级还没有设置课程表")
        }
    }

    // 获取课程表接口
    override fun getTimeTableSubject(data: TimeTableInfoBean) {
        mSubjectDetailMorningListBean.clear()
        mSubjectDetailNoonListBean.clear()
        // 显示日期
        val beginData = data.beginDate
        val endData = data.endDate
        val beginSec = DateUtils.stringToLong(beginData,"yyyy-MM-dd")
        val endSec = DateUtils.stringToLong(endData,"yyyy-MM-dd")
        val timeList = arrayListOf<String>()
        for (time in beginSec..endSec step 1000 * 60 * 60 * 24) {
            val weekTime = DateUtils.format(Date(time), "yy-MM-dd")
            timeList.add(weekTime.substring(6))
        }
        tv1day.text = timeList[0]
        tv2day.text = timeList[1]
        tv3day.text = timeList[2]
        tv4day.text = timeList[3]
        tv5day.text = timeList[4]
        tv6day.text = timeList[5]
        tv7day.text = timeList[6]
        // 课程表主题数据
        if (data.timeTableDetailList != null && data.timeTableDetailList.size > 0) {
            for (item in data.timeTableDetailList) {
                // 获取上午所有的课 并且 是选中日期的课
                if (item.lessonType == 1 && item.weekDay == weekend) {
                    tvMorning.setVisible(true)
                    tvNoon.setVisible(true)
                    mSubjectDetailMorningListBean.add(SubjectDetailBean(item))
                } else if (item.lessonType == 2 && item.weekDay == weekend) {
                    tvMorning.setVisible(true)
                    tvNoon.setVisible(true)
                    mSubjectDetailNoonListBean.add(SubjectDetailBean(item))
                } else {
                    tvMorning.setVisible(false)
                    tvNoon.setVisible(false)
                }
                mSubjectDetailMorningAdapter.notifyDataSetChanged()
                mSubjectDetailNoonAdapter.notifyDataSetChanged()
            }
        }
    }
}