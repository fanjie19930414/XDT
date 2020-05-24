package com.kapok.apps.maple.xdt.homework.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.CheckHomeWorkTeacherAdapter
import com.kapok.apps.maple.xdt.homework.adapter.CheckCommitHomeWorkListAdapter
import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.CommitHomeWorkClassInfoBean
import com.kapok.apps.maple.xdt.homework.bean.CommitHomeWorkStudentInfoBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListItemBean
import com.kapok.apps.maple.xdt.homework.presenter.CheckHomeWorkTeacherPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.CheckHomeWorkTeacherView
import com.kapok.apps.maple.xdt.utils.PhotoShowActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.custom.CustomHomeWorkDataDialog
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.*
import kotlinx.android.synthetic.main.activity_check_homework_teacher.*
import java.io.Serializable

/**
 * 查看作业 教师端
 */
@SuppressLint("SetTextI18n")
class CheckHomeWorkTeacherActivity : BaseMVPActivity<CheckHomeWorkTeacherPresenter>(),
    CheckHomeWorkTeacherView {
    // 传入的列表信息
    private lateinit var homeWorkItemBean: HomeWorkListItemBean
    // 作业Id
    private var workId: Int = -1
    // 作业创建时间
    private lateinit var createTime: String
    // 科目
    private lateinit var subjectName: String
    // 接口返回的图片列表
    private lateinit var checkHomeWorkPicList: MutableList<String>
    private lateinit var checkHomeWorkTeacherAdapter: CheckHomeWorkTeacherAdapter
    //  未提交 / 提交状态
    private var submitState = 1
    // 未提交 / 提交列表
    private lateinit var noSubmit: TabLayout.Tab
    private lateinit var haveSubmit: TabLayout.Tab
    private lateinit var dataList: MutableList<MultiItemEntity>
    private lateinit var commitAdapter: CheckCommitHomeWorkListAdapter
    // emptyView
    private lateinit var tvEmptyContent: TextView
    private var showDialog = false
    // 三个点Dialog
    private lateinit var checkHomeWorkBottomDialog: CustomCancelBottomDialog
    private lateinit var confirmDialog: CancelConfirmDialog
    // 作业详情Bean
    private lateinit var checkHomeWorkBean: CheckHomeWorkTeacherBean
    // 修改时间DiaLog
    private lateinit var homeWorkTimeDialog: CustomHomeWorkDataDialog
    private lateinit var selectTime: String
    // 还未被提醒的学生Id
    private lateinit var unRemindStudentsList: MutableList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_homework_teacher)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = CheckHomeWorkTeacherPresenter(this)
        mPresenter.mView = this
        // 接收传参
        homeWorkItemBean = intent.getParcelableExtra("homeWorkItemInfo")
        workId = homeWorkItemBean.workId
        createTime = homeWorkItemBean.gmtCreate
        subjectName = homeWorkItemBean.subjectName
        createTime = createTime.substring(5, 10).replace("-", "月")
        // 头像
        if (homeWorkItemBean.teacherAvatar != null && homeWorkItemBean.teacherAvatar!!.isNotEmpty()) {
            GlideUtils.loadImage(
                this,
                homeWorkItemBean.teacherAvatar!!,
                civCheckHomeWorkTeacherIcon
            )
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civCheckHomeWorkTeacherIcon)
        }
        // 姓名
        if (homeWorkItemBean.isTeacherLeader) {
            tvCheckHomeWorkTeacherName.text = homeWorkItemBean.teacherName + "(班主任)"
        } else {
            tvCheckHomeWorkTeacherName.text = homeWorkItemBean.teacherName
        }
        // 标题
        tvCheckHomeWorkTitle.text = createTime + "日" + subjectName + "作业"
        // 配置图片Rv
        checkHomeWorkPicList = arrayListOf()
        rvCheckHomeWorkPic.layoutManager = GridLayoutManager(this, 3)
        checkHomeWorkTeacherAdapter = CheckHomeWorkTeacherAdapter(this, checkHomeWorkPicList)
        rvCheckHomeWorkPic.adapter = checkHomeWorkTeacherAdapter
        // 配置Tab
        noSubmit = tabCheckHomeWork.newTab()
        haveSubmit = tabCheckHomeWork.newTab()
        noSubmit.text = "未提交" + "(" + 0 + ")"
        haveSubmit.text = "已提交" + "(" + 0 + ")"
        tabCheckHomeWork.addTab(noSubmit, 0,true)
        tabCheckHomeWork.addTab(haveSubmit, 1,false)
        // 配置列表Rv
        dataList = arrayListOf()
        unRemindStudentsList = arrayListOf()
        commitAdapter = CheckCommitHomeWorkListAdapter(dataList)
        rvCommitHomeWork.adapter = commitAdapter
        rvCommitHomeWork.layoutManager = LinearLayoutManager(this)
        rvCommitHomeWork.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1),
                resources.getColor(com.kotlin.baselibrary.R.color.login_xdt_view_line)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(this)
            .inflate(R.layout.layout_class_list_empty2, rvCommitHomeWork, false)
        tvEmptyContent = emptyView.findViewById(R.id.tvEmptyContent)
        if (submitState == 1) {
            tvEmptyContent.text = "所有人都已提交!"
        } else {
            tvEmptyContent.text = "还没有学生提交作业哦~"
        }
        commitAdapter.emptyView = emptyView
    }

    override fun onResume() {
        super.onResume()
        // 调用查看作业接口
        mPresenter.checkHomeWorkTeacher(AppPrefsUtils.getInt("userId"), workId)
    }

    private fun initListener() {
        // 返回
        ivCheckHomeWorkBack.setOnClickListener { finish() }
        // 三个点
        ivCheckHomeWorkSetting.setOnClickListener {
            // dialog
            checkHomeWorkBottomDialog = CustomCancelBottomDialog(this, R.style.BottomDialog)
            if (tvCheckHomeWorkStatus.text == "进行中") {
                checkHomeWorkBottomDialog.addItem(
                    "修改作业截止日期",
                    R.color.text_xdt,
                    View.OnClickListener {
                        homeWorkTimeDialog =
                            CustomHomeWorkDataDialog(this, R.style.BottomDialog)
                        homeWorkTimeDialog.setTitle("修改作业截止时间")
                        homeWorkTimeDialog.setSelectedTime(
                            DateUtils.getYear().toString(),
                            DateUtils.getMonth().toString(),
                            DateUtils.getDay().toString(),
                            DateUtils.getHour().toString(),
                            DateUtils.getMinute().toString()
                        )
                        homeWorkTimeDialog.show()
                        homeWorkTimeDialog.setOnselectDataListener(object :
                            CustomHomeWorkDataDialog.SelectDataListener {
                            override fun selectData(
                                year: String,
                                month: String,
                                day: String,
                                hour: String,
                                minute: String
                            ) {
                                selectTime = "$year-$month-$day $hour:$minute"
                                val current = DateUtils.curTime + 5 * 60 * 1000
                                val selectedTime = DateUtils.paseDateTomillise(selectTime)
                                if (selectedTime >= current) {
                                    mPresenter.editHomeWorkTime(
                                        "$selectTime:00",
                                        AppPrefsUtils.getInt("userId").toString().toInt()
                                    )
                                    checkHomeWorkBottomDialog.dismiss()
                                } else {
                                    ToastUtils.showMsg(this@CheckHomeWorkTeacherActivity,"截止时间至少需大于当前时间5分钟")
                                }
                            }
                        })
                    })
                checkHomeWorkBottomDialog.addItem(
                    "立即结束",
                    R.color.text_xdt,
                    View.OnClickListener {
                        mPresenter.finishHomeWork(workId.toString())
                        checkHomeWorkBottomDialog.dismiss()
                    })
            } else {
                checkHomeWorkBottomDialog.addItem(
                    "再次发布",
                    R.color.text_xdt,
                    View.OnClickListener {
                        val imagesList = arrayListOf<String>()
                        imagesList.addAll(checkHomeWorkPicList)
                        val intent = Intent(this, SendHomeWorkActivity::class.java)
                        intent.putExtra("fromCheckHomeWork", true)
                        intent.putExtra("homeWorkInfo", checkHomeWorkBean)
                        intent.putExtra("homeWorkImage", imagesList)
                        startActivity(intent)
                        checkHomeWorkBottomDialog.dismiss()
                        AppManager.instance.finishActivity(this)
                    })
                checkHomeWorkBottomDialog.addItem(
                    "删除",
                    R.color.xdt_exit_text,
                    View.OnClickListener {
                        confirmDialog = CancelConfirmDialog(
                            this, R.style.BottomDialog
                            , "作业删除后，所有人无法查看该作业，请确认是否删除？", ""
                        )
                        confirmDialog.show()
                        confirmDialog.setOnClickConfirmListener(object :
                            CancelConfirmDialog.ClickConfirmListener {
                            override fun confirm() {
                                // 删除作业接口
                                mPresenter.deleteHomeWork(
                                    AppPrefsUtils.getInt("userId").toString(),
                                    workId.toString()
                                )
                                confirmDialog.dismiss()
                            }
                        })
                        checkHomeWorkBottomDialog.dismiss()
                    })
            }
            checkHomeWorkBottomDialog.show()
        }
        // 图片点击查看大图
        checkHomeWorkTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent =
                    Intent(this@CheckHomeWorkTeacherActivity, PhotoShowActivity::class.java)
                intent.putExtra("showUrlList", checkHomeWorkPicList as Serializable)
                intent.putExtra("isUrlList", true)
                intent.putExtra("index", position)
                startActivity(intent)
            }
        // 未选中
        tabCheckHomeWork.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab?> {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        noSubmitTab()
                    }
                    1 -> {
                        submitTab()
                    }
                }
            }
        })
        // 一键提醒
        btRemind.setOnClickListener {
            showDialog = false
            mPresenter.remindHomeWork(unRemindStudentsList,AppPrefsUtils.getInt("userId"),workId)
        }
        // 子Item点击事件
        commitAdapter.setOnAdapterClickListener(object : CheckCommitHomeWorkListAdapter.AdapterClickListener {
            override fun onAdapterClick(bean: CommitHomeWorkStudentInfoBean) {
                if (submitState == 1) {
                    unRemindStudentsList.clear()
                    unRemindStudentsList.add(bean.studentId)
                    mPresenter.remindHomeWork(unRemindStudentsList,AppPrefsUtils.getInt("userId"),workId)
                } else {
                    val intent = Intent(this@CheckHomeWorkTeacherActivity,StudentAnswerActivity::class.java)
                    intent.putExtra("workId",workId)
                    intent.putExtra("studentId",bean.studentId)
                    intent.putExtra("classId",bean.classId)
                    intent.putExtra("studentName",bean.studentName)
                    if (bean.studentAvatar != null && bean.studentAvatar.isNotEmpty()) {
                        intent.putExtra("studentPic",bean.studentAvatar)
                    } else {
                        intent.putExtra("studentPic","")
                    }
                    startActivity(intent)
                }
            }
        })
    }

    private fun submitTab() {
        // 已提交
        submitState = 2
        tvEmptyContent.text = "还没有学生提交作业哦~"
        mPresenter.getCommitHomeWorkList(
            submitState,
            AppPrefsUtils.getInt("userId"),
            workId,
            showDialog
        )
    }

    private fun noSubmitTab() {
        // 未提交
        submitState = 1
        tvEmptyContent.text = "所有人都已提交!"
        mPresenter.getCommitHomeWorkList(
            submitState,
            AppPrefsUtils.getInt("userId"),
            workId,
            showDialog
        )
    }

    // 获取老师作业详情接口回调
    override fun getHomeWorkTeacherDetail(bean: CheckHomeWorkTeacherBean) {
        checkHomeWorkBean = bean
        // 标题
        tvCheckHomeWorkContent.text = bean.title
        // 创建时间
        tvCheckHomeWorkStartTime.text = bean.gmtCreate?.substring(0, bean.gmtCreate.length - 3)
        // 作业状态
        tvCheckHomeWorkType.text = bean.workTypeDesc
        // 进行中/已结束
        if (bean.state == 1) {
            tvCheckHomeWorkStatus.text = "进行中"
            tvCheckHomeWorkStatus.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
            tvCheckHomeWorkStatus.setBackgroundResource(R.drawable.shape_background_corner_blue_fill)
        } else {
            tvCheckHomeWorkStatus.text = "已结束"
            tvCheckHomeWorkStatus.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvCheckHomeWorkStatus.setBackgroundResource(R.drawable.shape_background_corner_hint_fill)
        }
        // 截至时间
        tvCheckHomeWorkEndTime.text =
            "截至时间：" + bean.deadline?.substring(0, bean.deadline.length - 3)
        // 内容
        checkHomeWorkMore.setText(bean.content)
        // 图片
        if (bean.images.isNotEmpty()) {
            val picList = bean.images.split(",")
            checkHomeWorkPicList.clear()
            for (item in picList) {
                checkHomeWorkPicList.add(item)
            }
        }
        checkHomeWorkTeacherAdapter.notifyDataSetChanged()
        // 标签
        noSubmit.text = "未提交" + "(" + bean.unSubStudentCount + ")"
        haveSubmit.text = "已提交" + "(" + bean.subStudentCount + ")"
        // 每次调用详情接口 需重新刷未提交学生列表接口
        when(submitState) {
            1 -> {
                noSubmitTab()
            }
            2 -> {
                submitTab()
            }
        }
    }

    // 获取 未提交/提交 作业的列表
    override fun getCommitHomeWorkListBean(list: MutableList<CommitHomeWorkClassInfoBean>?) {
        showDialog = true
        dataList.clear()
        if (list != null && list.size > 0) {
            if (submitState == 1) {
                btRemind.setVisible(true)
            } else {
                btRemind.setVisible(false)
            }
            for (item in list) {
                val classInfoBean = CommitHomeWorkClassInfoBean(
                    item.classId,
                    item.className,
                    item.grade,
                    item.gradeId,
                    item.schoolId,
                    item.schoolName,
                    item.startYear,
                    item.studentCount,
                    item.studentDetails
                )
                for (itemStudent in item.studentDetails) {
                    if (itemStudent.studentAvatar != null) {
                        val studentInfoBean = CommitHomeWorkStudentInfoBean(
                            itemStudent.classId,
                            itemStudent.commentStatus,
                            itemStudent.studentAvatar,
                            itemStudent.studentId,
                            itemStudent.studentName
                        )
                        classInfoBean.addSubItem(studentInfoBean)
                    } else {
                        val studentInfoBean = CommitHomeWorkStudentInfoBean(
                            itemStudent.classId,
                            itemStudent.commentStatus,
                            null,
                            itemStudent.studentId,
                            itemStudent.studentName
                        )
                        classInfoBean.addSubItem(studentInfoBean)
                    }
                    // 判断哪些学生还未被提醒
                    if (submitState == 1 && itemStudent.commentStatus == 0) {
                        unRemindStudentsList.add(itemStudent.studentId)
                    }
                }
                dataList.add(classInfoBean)
            }
        } else {
            btRemind.setVisible(false)
        }
        commitAdapter.setSubmitState(submitState)
        commitAdapter.expandAll()
        commitAdapter.notifyDataSetChanged()
        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
    }

    // 修改截至日期
    override fun editTimeHomeWorkResult(msg: String) {
        ToastUtils.showMsg(this, msg)
        // 调用查看作业接口刷新数据
        mPresenter.checkHomeWorkTeacher(AppPrefsUtils.getInt("userId"), workId)
    }

    // 结束作业回调
    override fun finishHomeWorkResult(msg: String) {
        ToastUtils.showMsg(this, msg)
        // 调用查看作业接口刷新数据
        mPresenter.checkHomeWorkTeacher(AppPrefsUtils.getInt("userId"), workId)
    }

    // 删除作业回调
    override fun deleteHomeWorkResult(msg: String) {
        ToastUtils.showMsg(this, msg)
        finish()
    }

    // 一键提醒回调
    override fun remindHomeWorkResult(msg: String) {
        ToastUtils.showMsg(this,msg)
        noSubmitTab()
    }
}