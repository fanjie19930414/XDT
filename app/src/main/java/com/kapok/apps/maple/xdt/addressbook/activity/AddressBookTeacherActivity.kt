package com.kapok.apps.maple.xdt.addressbook.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookStudentListAdapter
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookTeacherApplyAdapter
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookTeacherListAdapter
import com.kapok.apps.maple.xdt.addressbook.bean.*
import com.kapok.apps.maple.xdt.addressbook.presenter.AddressBookTeacherPresenter
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookTeacherView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_addressbook_teacher.*
import kotlinx.android.synthetic.main.dialog_teacher_list.*

/**
 * 通讯录页面老师端（家长端  区别在于isHeadTeacher）
 */
@SuppressLint("SetTextI18n")
class AddressBookTeacherActivity : BaseMVPActivity<AddressBookTeacherPresenter>(),
    AddressBookTeacherView {
    // 传参
    private var classId: Int = 0
    private var isHeaderTeacher: Boolean = false
    // 申请列表List
    private lateinit var applyList: MutableList<AddressBookNoHandel>
    private lateinit var applyPicAdapter: AddressBookTeacherApplyAdapter
    // 老师详情列表
    private lateinit var teacherDetailList: MutableList<AddressBookTeacherDetails>
    private lateinit var teacherDetailAdapter: AddressBookTeacherListAdapter
    // 学生详情列表
    private lateinit var studentDetailList: MutableList<AddressBookStudentDetails>
    private lateinit var studentDetailAdapter: AddressBookStudentListAdapter
    // 点击状态
    private var teacherOpen = true
    private var studentOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressbook_teacher)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = AddressBookTeacherPresenter(this)
        mPresenter.mView = this
        // 传参
        classId = intent.getIntExtra("classId", 0)
        isHeaderTeacher = intent.getBooleanExtra("isHeaderTeacher", false)
        if (isHeaderTeacher) {
            llAddressBookApply.setVisible(true)
        } else {
            llAddressBookApply.setVisible(false)
        }
        teacherState()
        studentState()
        // 配置新的申请Rv
        applyList = arrayListOf()
        rvAddressBookAsk.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        applyPicAdapter = AddressBookTeacherApplyAdapter(this, applyList)
        rvAddressBookAsk.adapter = applyPicAdapter
        // 配置老师详情Rv
        teacherDetailList = arrayListOf()
        rvAddressBookTeacherInfo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        teacherDetailAdapter = AddressBookTeacherListAdapter(this, teacherDetailList)
        rvAddressBookTeacherInfo.adapter = teacherDetailAdapter
        // 配置学生详情Rv
        studentDetailList = arrayListOf()
        rvAddressBookStudentInfo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        studentDetailAdapter = AddressBookStudentListAdapter(this, studentDetailList)
        rvAddressBookStudentInfo.adapter = studentDetailAdapter
    }

    override fun onResume() {
        super.onResume()
        // 调用申请列表接口
        if (isHeaderTeacher) {
            mPresenter.getApplyList(classId, AppPrefsUtils.getInt("userId"))
        }
        // 调用老师和学生通讯录列表接口
        mPresenter.getAddressBookList(classId, AppPrefsUtils.getInt("userId"))
    }


    private fun initListener() {
        // 返回
        ivAddressBookBackTeacher.setOnClickListener { finish() }
        // 邀请
        ivAddressBookSettingTeacher.setOnClickListener {
            ToastUtils.showMsg(
                this@AddressBookTeacherActivity,
                "邀请"
            )
        }
        // 申请页
        llAddressBookApply.setOnClickListener {
            val intent = Intent(
                this@AddressBookTeacherActivity,
                AddressBookNewPersonActivity::class.java
            )
            intent.putExtra("classId", classId)
            startActivity(intent)
        }
        // 老师点击
        rlAddressBookTeacherList.setOnClickListener {
            teacherOpen = !teacherOpen
            teacherState()
        }
        // 学生点击
        rlAddressBookStudentList.setOnClickListener {
            studentOpen = !studentOpen
            studentState()
        }
        // 老师点击事件
        teacherDetailAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(
                this@AddressBookTeacherActivity,
                AddressBookTeacherDetailActivity::class.java
            )
            intent.putExtra("classId", classId)
            intent.putExtra("teacherId", teacherDetailList[position].userId)
            intent.putExtra("teacherName", teacherDetailList[position].realName)
            startActivity(intent)
        }
        // 学生点击事件
        studentDetailAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(
                this@AddressBookTeacherActivity,
                AddressBookChildDetailActivity::class.java
            )
            intent.putExtra("studentId", studentDetailList[position].userId)
            intent.putExtra("classId", classId)
            intent.putExtra("isHeaderTeacher", isHeaderTeacher)
            intent.putExtra("studentName", studentDetailList[position].realName)
            startActivity(intent)
        }
    }

    // 学生列表展开状态
    private fun studentState() {
        if (studentOpen) {
            rvAddressBookStudentInfo.setVisible(true)
            ivAddressBookStudentArrow.setImageResource(R.mipmap.prev_list_d)
        } else {
            rvAddressBookStudentInfo.setVisible(false)
            ivAddressBookStudentArrow.setImageResource(R.mipmap.prev_list_r)
        }
    }

    // 老师列表展开状态
    private fun teacherState() {
        if (teacherOpen) {
            rvAddressBookTeacherInfo.setVisible(true)
            ivAddressBookTeacherArrow.setImageResource(R.mipmap.prev_list_d)
        } else {
            rvAddressBookTeacherInfo.setVisible(false)
            ivAddressBookTeacherArrow.setImageResource(R.mipmap.prev_list_r)
        }
    }

    // 申请列表回调
    override fun getApplyList(list: AddressBookApplyListBean) {
        applyList.clear()
        if (list.processingApprovals != null && list.processingApprovals.size > 0) {
            applyList.addAll(list.processingApprovals)
            applyPicAdapter.notifyDataSetChanged()
            tvAddressBookAsk.setVisible(true)
            tvAddressBookAsk.text = list.processingApprovals.size.toString()
        } else {
            tvAddressBookAsk.setVisible(false)
        }
    }

    // 老师获取班级通讯录列表接口回调
    override fun getAddressDetails(bean: AddressBookDetails) {
        teacherDetailList.clear()
        studentDetailList.clear()
        // 老师
        if (bean.teacherDetails != null && bean.teacherDetails.size > 0) {
            tvAddressBookTeacherNum.text = "老师" + "(" + bean.teacherDetails.size + "人)"
            teacherDetailList.addAll(bean.teacherDetails)
            teacherDetailAdapter.notifyDataSetChanged()
        }
        // 学生
        if (bean.studentDetails != null && bean.studentDetails.size > 0) {
            tvAddressBookStudentNum.text = "学生" + "(" + bean.studentDetails.size + "人)"
            studentDetailList.addAll(bean.studentDetails)
            studentDetailAdapter.notifyDataSetChanged()
        }
    }
}