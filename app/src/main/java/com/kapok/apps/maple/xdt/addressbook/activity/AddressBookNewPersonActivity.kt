package com.kapok.apps.maple.xdt.addressbook.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookNewPersonApplyAdapter
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookNewPersonHandleAdapter
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookApplyListBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookHaveHandel
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookNoHandel
import com.kapok.apps.maple.xdt.addressbook.presenter.AddressBookNewPersonPresenter
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookNewPersonView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_addressbook_newperson.*
import kotlinx.android.synthetic.main.fragment_class_list_parent.*

/**
 * 通讯录 查看新成员页面
 */
@SuppressLint("SetTextI18n")
class AddressBookNewPersonActivity : BaseMVPActivity<AddressBookNewPersonPresenter>(),
    AddressBookNewPersonView {
    // 传参
    private var classId: Int = -1
    // 申请列表List
    private lateinit var applyList: MutableList<AddressBookNoHandel>
    private lateinit var applyAdapter: AddressBookNewPersonApplyAdapter
    // 已处理列表
    private lateinit var handleList: MutableList<AddressBookHaveHandel>
    private lateinit var handleAdapter: AddressBookNewPersonHandleAdapter
    // 空列表
    private lateinit var emptyView: View
    private lateinit var tvClassEmptyAdd: TextView
    private lateinit var tvClassEmptyContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressbook_newperson)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = AddressBookNewPersonPresenter(this)
        mPresenter.mView = this
        // 传参
        classId = intent.getIntExtra("classId", -1)
        // 配置申请中Rv
        applyList = arrayListOf()
        applyAdapter = AddressBookNewPersonApplyAdapter(this, applyList)
        rvNoHandle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvNoHandle.adapter = applyAdapter
        // 配置已处理Rv
        handleList = arrayListOf()
        handleAdapter = AddressBookNewPersonHandleAdapter(this, handleList)
        rvHaveHandle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvHaveHandle.adapter = handleAdapter
        // emptyView
        emptyView = LayoutInflater.from(this)
            .inflate(R.layout.layout_class_list_empty, rvClassListParent, false)
        tvClassEmptyContent = emptyView.findViewById(R.id.tvEmptyContent)
        tvClassEmptyAdd = emptyView.findViewById(R.id.tvClassEmptyAdd)
        tvClassEmptyContent.text = "还没有人申请加入班级哦~快去"
        tvClassEmptyAdd.text = "邀请"
    }

    override fun onResume() {
        super.onResume()
        // 调用接口
        mPresenter.getApplyList(classId, AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 返回
        ivBackNewPerson.setOnClickListener { finish() }
        // 邀请
        ivSettingNewPerson.setOnClickListener {
            ToastUtils.showMsg(
                this@AddressBookNewPersonActivity,
                "邀请"
            )
        }
        // 列表点击 同意
        applyAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                when (view?.id) {
                    R.id.tvAgreeApply -> {
                        // 调用同意接口 (1 同意 2 拒绝)
                        mPresenter.approvalApply(1,applyList[position].userId,classId,AppPrefsUtils.getInt("userId"))
                    }
                }
            }
        applyAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(this@AddressBookNewPersonActivity,AddressBookHandleDetailActivity::class.java)
                intent.putExtra("isHandle",false)
                intent.putExtra("identityType",applyList[position].identityType)
                intent.putExtra("userId",applyList[position].userId)
                intent.putExtra("handleState","")
                intent.putExtra("classId",classId)
                startActivity(intent)
            }
        // 列表点击 已处理
        handleAdapter.setOnItemClickListener { _, _, position ->
            val intent = Intent(this@AddressBookNewPersonActivity,AddressBookHandleDetailActivity::class.java)
            intent.putExtra("isHandle",true)
            intent.putExtra("identityType",handleList[position].identityType)
            intent.putExtra("userId",handleList[position].userId)
            intent.putExtra("handleState",handleList[position].stateDesc)
            intent.putExtra("classId",classId)
            startActivity(intent)
        }
    }

    // 获取已处理与待处理的列表
    override fun getApplyList(bean: AddressBookApplyListBean) {
        // 待处理
        applyList.clear()
        if (bean.processingApprovals != null && bean.processingApprovals.size > 0) {
            rvNoHandle.setVisible(true)
            applyList.addAll(bean.processingApprovals)
            applyAdapter.notifyDataSetChanged()
            rlNoHandle.setVisible(true)
            tvNoHandle.text = "待处理" + "(" + bean.processingApprovals.size.toString() + ")"
        } else {
            rvNoHandle.setVisible(false)
            rlNoHandle.setVisible(false)
        }
        // 已处理
        handleList.clear()
        if (bean.processedApprovals != null && bean.processedApprovals.size > 0) {
            rlHaveHandel.setVisible(true)
            handleList.addAll(bean.processedApprovals)
            handleAdapter.notifyDataSetChanged()
        } else {
            rlHaveHandel.setVisible(false)
        }
        // 都没有 展示空列表
        if (applyList.size == 0 && handleList.size == 0) {
            handleAdapter.emptyView = emptyView
        }
    }

    // 同意申请接口
    override fun approvalResult(msg: String) {
        ToastUtils.showMsg(this,msg)
        //刷新接口
        mPresenter.getApplyList(classId, AppPrefsUtils.getInt("userId"))
    }
}