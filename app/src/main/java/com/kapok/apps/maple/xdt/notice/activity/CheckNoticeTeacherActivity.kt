package com.kapok.apps.maple.xdt.notice.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.CheckHomeWorkTeacherAdapter
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailTeacherBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeListItemBean
import com.kapok.apps.maple.xdt.notice.presenter.CheckNoticeTeacherPresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.CheckNoticeTeacherView
import com.kapok.apps.maple.xdt.utils.PhotoShowActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_check_notice_teacher.*
import java.io.Serializable

/**
 * 查看通知班主任端
 */
@SuppressLint("SetTextI18n")
class CheckNoticeTeacherActivity : BaseMVPActivity<CheckNoticeTeacherPresenter>(),
    CheckNoticeTeacherView {
    // 传入的列表信息
    private lateinit var noticeItemBean: NoticeListItemBean
    private var workId = 0
    private var classId = 0
    private var noReceiveNum = 0
    private var haveReceiveNum = 0
    // 三个点Dialog
    private lateinit var noticeSettingDialog: CustomCancelBottomDialog
    // 接口返回的图片列表
    private lateinit var checkNoticePicList: MutableList<String>
    private lateinit var checkNoticeTeacherAdapter: CheckHomeWorkTeacherAdapter
    // 没有回复/查看的同学Ids
    private lateinit var studentIds: MutableList<Int>
    // 查看还是回复
    private var isReceipt: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_notice_teacher)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = CheckNoticeTeacherPresenter(this)
        mPresenter.mView = this
        // 接收传参
        noticeItemBean = intent.getParcelableExtra("noticeItemInfo")
        workId = noticeItemBean.workId
        if (noticeItemBean.classId != null) {
            classId = noticeItemBean.classId as Int
        }
        // 配置作业图片Rv
        checkNoticePicList = arrayListOf()
        rvCheckNoticePicTeacher.layoutManager = GridLayoutManager(this, 3)
        checkNoticeTeacherAdapter = CheckHomeWorkTeacherAdapter(this, checkNoticePicList)
        rvCheckNoticePicTeacher.adapter = checkNoticeTeacherAdapter
        // 未回复的学生Id
        studentIds = arrayListOf()
        // 获取查看通知详情
        mPresenter.checkNoticeTeacher(classId,AppPrefsUtils.getInt("userId"), workId)
        mPresenter.getTeacherNoticeReceiptList(1,AppPrefsUtils.getInt("userId"),workId)
    }

    private fun initListener() {
        // 返回
        ivCheckNoticeBackTeacher.setOnClickListener { finish() }
        // 三个点
        ivCheckNoticeSettingTeacher.setOnClickListener {
            noticeSettingDialog =
                CustomCancelBottomDialog(this, R.style.BottomDialog)
            noticeSettingDialog.addItem("再次发布", R.color.text_xdt, View.OnClickListener {
                // 再次发布
                mPresenter.publishNoticeAgain(AppPrefsUtils.getInt("userId"),workId)
                noticeSettingDialog.dismiss()
            })
            noticeSettingDialog.addItem("撤回通知", R.color.xdt_exit_text, View.OnClickListener {
                // 撤回通知
                mPresenter.delectNotice(AppPrefsUtils.getInt("userId"),workId)
                noticeSettingDialog.dismiss()
            })
            noticeSettingDialog.show()
        }
        // 图片点击查看大图
        checkNoticeTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(this, PhotoShowActivity::class.java)
                intent.putExtra("showUrlList", checkNoticePicList as Serializable)
                intent.putExtra("isUrlList", true)
                intent.putExtra("index", position)
                startActivity(intent)
            }
        // 数据统计
        llCheckNoticeData.setOnClickListener {
            val intent = Intent(this@CheckNoticeTeacherActivity,DataStatisticsActivity::class.java)
            intent.putExtra("workId",workId)
            intent.putExtra("noReceiveNum",noReceiveNum)
            intent.putExtra("haveReceiveNum",haveReceiveNum)
            intent.putExtra("isReceipt",isReceipt)
            startActivity(intent)
        }
        // 一键提醒
        btNoticeTeacher.setOnClickListener {
            if (studentIds.size > 0) {
                mPresenter.remindNotice(studentIds,AppPrefsUtils.getInt("userId"),workId)
            } else {
                ToastUtils.showMsg(this@CheckNoticeTeacherActivity,"没有学生需要提醒")
            }
        }
    }

    // 获取通知详情回调
    override fun getNoticeDetailTeacherBean(bean: NoticeDetailTeacherBean) {
        // 页面Title
        tvCheckNoticeTitleTeacher.text = bean.gmtCreate.substring(5, 10).replace("-", "月") + "日班级通知"
        // 通知标题
        tvCheckNoticeContentTeacher.text = bean.title
        // 班级信息
        tvCheckNoticeClassNameTeacher.text =
            bean.grade + bean.className + "班(" + bean.startYear + "级)"
        // 创建时间
        tvCheckNoticeStartTimeTeacher.text = bean.gmtCreate.substring(0, 16)
        // 头像
        GlideUtils.loadUrlHead(this, noticeItemBean.teacherAvatar ?: "", civCheckNoticeTeacherIcon)
        // 姓名
        if (noticeItemBean.isTeacherLeader) {
            if (noticeItemBean.subjectName != null) {
                tvCheckNoticeTeacherNameParent.text =
                    noticeItemBean.teacherName + "(班主任)"
            } else {
                tvCheckNoticeTeacherNameParent.text =
                    noticeItemBean.teacherName + "(班主任)"
            }
        } else {
            tvCheckNoticeTeacherNameParent.text =
                noticeItemBean.teacherName
        }
        // 内容
        checkNoticeTeacher.text = bean.content
        // 图片
        if (bean.images.isNotEmpty()) {
            val picList = bean.images.split(",")
            checkNoticePicList.clear()
            for (item in picList) {
                checkNoticePicList.add(item)
            }
        }
        checkNoticeTeacherAdapter.notifyDataSetChanged()
        // 数据统计
        if (bean.isReceipt) {
            haveReceiveNum = bean.receiptCount
            noReceiveNum = bean.noReceiptCount
        } else {
            haveReceiveNum = bean.readCount
            noReceiveNum = bean.noReadCount
        }
        // 是否需要回执
        isReceipt = bean.isReceipt
        if (bean.isReceipt) {
            tvNoticeReplayTitle.text = "已回复" + bean.receiptCount + "人，未回复" + bean.noReceiptCount + "人"
        } else {
            tvNoticeReplayTitle.text = "已查看" + bean.readCount + "人，未查看" + bean.noReadCount + "人"
        }
        progressBarNoticeTeacher.max = bean.receiptCount + bean.noReceiptCount
        progressBarNoticeTeacher.progress = bean.receiptCount
        if (bean.isReceipt) {
            tvNoticeTeacherTotalNum.text = (bean.receiptCount + bean.noReceiptCount).toString() + "人"
        } else {
            tvNoticeTeacherTotalNum.text = (bean.readCount + bean.noReadCount).toString() + "人"
        }
    }

    // 再次发布回调
    override fun getRePubResult(msg: String) {
        ToastUtils.showMsg(this,msg)
    }

    // 一键提醒回调
    override fun remindNotice(msg: String) {
        ToastUtils.showMsg(this,msg)
    }

    // 查看回复详情接口
    override fun getTeacherDataStatisticsList(t: MutableList<NoticeDataStatisticsBean>?) {
        studentIds.clear()
        if (t != null && t.size > 0) {
            for (item in t) {
                if (item.studentDetails != null && item.studentDetails.size > 0) {
                    for (studentItem in item.studentDetails) {
                        studentIds.add(studentItem.studentId)
                    }
                }
            }
        }
    }

    // 撤回通知回调
    override fun deleteNotice(msg: String) {
        ToastUtils.showMsg(this,msg)
        finish()
    }
}