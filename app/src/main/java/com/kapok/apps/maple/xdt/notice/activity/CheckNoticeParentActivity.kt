package com.kapok.apps.maple.xdt.notice.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.CheckHomeWorkTeacherAdapter
import com.kapok.apps.maple.xdt.notice.adapter.ReceiveParentAdapter
import com.kapok.apps.maple.xdt.notice.bean.*
import com.kapok.apps.maple.xdt.notice.presenter.CheckNoticeParentPresenter
import com.kapok.apps.maple.xdt.notice.presenter.CheckNoticeTeacherPresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.CheckNoticeParentView
import com.kapok.apps.maple.xdt.notice.presenter.view.CheckNoticeTeacherView
import com.kapok.apps.maple.xdt.utils.PhotoShowActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_check_notice_parent.*
import java.io.Serializable

/**
 * 查看通知班主任端
 */
@SuppressLint("SetTextI18n")
class CheckNoticeParentActivity : BaseMVPActivity<CheckNoticeParentPresenter>(),
    CheckNoticeParentView {
    // 传入的列表信息
    private lateinit var noticeItemBean: NoticeListItemBean
    private var workId = 0
    private var classId = 0
    // 接口返回的图片列表
    private lateinit var checkNoticePicList: MutableList<String>
    private lateinit var checkNoticeTeacherAdapter: CheckHomeWorkTeacherAdapter
    // 回执列表
    private lateinit var receiveList: MutableList<ReceiveBean>
    private lateinit var receiveAdapter: ReceiveParentAdapter
    // 选中的Id
    private var chooseId: Int = -1
    // 是否已提交
    private var isSubmit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_notice_parent)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = CheckNoticeParentPresenter(this)
        mPresenter.mView = this
        // 接收传参
        noticeItemBean = intent.getParcelableExtra("noticeItemInfo")
        classId = intent.getIntExtra("classId", 0)
        workId = noticeItemBean.workId
        // 配置作业图片Rv
        checkNoticePicList = arrayListOf()
        rvCheckNoticePicParent.layoutManager = GridLayoutManager(this, 3)
        checkNoticeTeacherAdapter = CheckHomeWorkTeacherAdapter(this, checkNoticePicList)
        rvCheckNoticePicParent.adapter = checkNoticeTeacherAdapter
        // 配置回执Rv
        receiveList = arrayListOf()
        rvNoticeChoose.layoutManager = LinearLayoutManager(this)
        receiveAdapter = ReceiveParentAdapter(receiveList)
        rvNoticeChoose.adapter = receiveAdapter
        // 获取查看通知详情
        mPresenter.checkNoticeParent(
            classId,
            AppPrefsUtils.getInt("userId"),
            noticeItemBean.studentId,
            workId
        )
    }

    private fun initListener() {
        // 返回
        ivCheckNoticeBackParent.setOnClickListener { finish() }
        // 图片点击查看大图
        checkNoticeTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(this, PhotoShowActivity::class.java)
                intent.putExtra("showUrlList", checkNoticePicList as Serializable)
                intent.putExtra("isUrlList", true)
                intent.putExtra("index", position)
                startActivity(intent)
            }
        // 点击事件
        receiveAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                if (!isSubmit) {
                    when (view?.id) {
                        R.id.ivReceive -> {
                            for (item in receiveList) {
                                item.isChoose = false
                            }
                            receiveList[position].isChoose = !receiveList[position].isChoose
                            receiveAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        receiveAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                if (!isSubmit) {
                    for (item in receiveList) {
                        item.isChoose = false
                    }
                    receiveList[position].isChoose = !receiveList[position].isChoose
                    receiveAdapter.notifyDataSetChanged()
                }
            }
        // 提交
        btNoticeParent.setOnClickListener {
            for (item in receiveList) {
                if (item.isChoose) {
                    chooseId = item.receiptId
                    break
                }
            }
            if (chooseId != -1) {
                // 提交回执接口
                mPresenter.submitReceive(chooseId,noticeItemBean.studentId,AppPrefsUtils.getInt("userId"),workId)
            } else {
                ToastUtils.showMsg(this@CheckNoticeParentActivity, "您还没有选中回执内容")
            }
        }
    }

    // 获取通知详情回调
    override fun getNoticeDetailParentBean(bean: NoticeDetailParentBean) {
        // 页面Title
        tvCheckNoticeTitleParent.text = bean.gmtCreate.substring(5, 10).replace("-", "月") + "日班级通知"
        // 通知标题
        tvCheckNoticeContentParent.text = bean.title
        // 班级信息
        tvCheckNoticeClassNameParent.text =
            bean.grade + bean.className + "班(" + bean.startYear + "级)"
        // 创建时间
        tvCheckNoticeStartTimeParent.text = bean.gmtCreate.substring(0, 16)
        // 头像
        GlideUtils.loadUrlHead(this, noticeItemBean.teacherAvatar ?: "", civCheckNoticeParentIcon)
        // 姓名
        if (noticeItemBean.isTeacherLeader) {
            if (noticeItemBean.subjectName != null) {
                tvCheckNoticeParentNameParent.text =
                    noticeItemBean.teacherName + "(班主任)"
            } else {
                tvCheckNoticeParentNameParent.text =
                    noticeItemBean.teacherName + "(班主任)"
            }
        } else {
            tvCheckNoticeParentNameParent.text =
                noticeItemBean.teacherName
        }
        // 内容
        checkNoticeParent.text = bean.content
        // 图片
        if (bean.images.isNotEmpty()) {
            val picList = bean.images.split(",")
            checkNoticePicList.clear()
            for (item in picList) {
                checkNoticePicList.add(item)
            }
        }
        checkNoticeTeacherAdapter.notifyDataSetChanged()
        //  清空一下 防止重复添加
        receiveList.clear()
        // 判断是否需要回执
        if (bean.isReceipt) {
            if (bean.studentReceiptId == null) {
                isSubmit = false
                llCheckNoticeParentButton.setVisible(true)
                rvNoticeChoose.setVisible(true)
                llCheckNoticeChoose.setVisible(true)
                if (bean.receipts != null && bean.receipts.size > 0) {
                    for (item in bean.receipts) {
                        receiveList.add(ReceiveBean(false, item.receiptContent,item.receiptId))
                    }
                }
                receiveAdapter.notifyDataSetChanged()
            } else {
                isSubmit = true
                llCheckNoticeParentButton.setVisible(false)
                rvNoticeChoose.setVisible(true)
                llCheckNoticeChoose.setVisible(true)
                if (bean.receipts != null && bean.receipts.size > 0) {
                    for (item in bean.receipts) {
                        if (item.receiptId == bean.studentReceiptId) {
                            receiveList.add(ReceiveBean(true, item.receiptContent,item.receiptId))
                        } else {
                            receiveList.add(ReceiveBean(false, item.receiptContent,item.receiptId))
                        }
                    }
                }
                receiveAdapter.notifyDataSetChanged()
            }
        } else {
            llCheckNoticeParentButton.setVisible(false)
            rvNoticeChoose.setVisible(false)
            llCheckNoticeChoose.setVisible(false)
        }
    }

    // 提交回执接口
    override fun submitResult(t: String) {
        ToastUtils.showMsg(this, t)
        // 主要是刷新页面 获取查看通知详情
        mPresenter.checkNoticeParent(
            classId,
            AppPrefsUtils.getInt("userId"),
            noticeItemBean.studentId,
            workId
        )
    }
}