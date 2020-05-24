package com.kapok.apps.maple.xdt.addressbook.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookStudentParentListAdapter
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildDetailBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildParentBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetailBean
import com.kapok.apps.maple.xdt.addressbook.presenter.AddressBookHandleDetailPresenter
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookHandleDetailView
import com.kapok.apps.maple.xdt.home.activity.ParentUserInfoActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.*
import kotlinx.android.synthetic.main.activity_addressbook_handledetail.*

/**
 * 新成员申请页面
 */
class AddressBookHandleDetailActivity : BaseMVPActivity<AddressBookHandleDetailPresenter>(),
    AddressBookHandleDetailView {
    // 传参
    private var isHandle = false
    private var identityType = -1
    private var chooseUserId = -1
    private var classId = -1
    private var handleState = ""
    // 学生家长列表
    private lateinit var parentList: MutableList<AddressBookChildParentBean>
    private lateinit var childParentAdapter: AddressBookStudentParentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressbook_handledetail)
        initData()
        initListener()
    }

    private fun initListener() {
        // 返回
        ivAddressBookHandleBack.setOnClickListener { finish() }
        // 家长电话
        if (identityType == 0) {
            childParentAdapter.onItemChildClickListener =
                BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                    when (view?.id) {
                        R.id.ivAddressBookParentDetail -> {
                            CallPhoneUtils.callPhone(
                                this@AddressBookHandleDetailActivity,
                                parentList[position].telephone
                            )
                        }
                    }
                }
            // 家长信息
            childParentAdapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    val intent =
                        Intent(this, ParentUserInfoActivity::class.java)
                    intent.putExtra("parentUserId", parentList[position].userId)
                    intent.putExtra("studentId", chooseUserId)
                    startActivity(intent)
                }
        }
        if (!isHandle) {
            // 拒绝
            btRefuseApply.setOnClickListener {
                mPresenter.approvalApply(
                    2,
                    chooseUserId,
                    classId,
                    AppPrefsUtils.getInt("userId")
                )
            }
            // 同意
            btAgreeApply.setOnClickListener {
                mPresenter.approvalApply(
                    1,
                    chooseUserId,
                    classId,
                    AppPrefsUtils.getInt("userId")
                )
            }
        }
    }

    private fun initData() {
        mPresenter = AddressBookHandleDetailPresenter(this)
        mPresenter.mView = this
        // 接收传参
        isHandle = intent.getBooleanExtra("isHandle", false)
        identityType = intent.getIntExtra("identityType", -1)
        chooseUserId = intent.getIntExtra("userId", -1)
        classId = intent.getIntExtra("classId", classId)
        handleState = intent.getStringExtra("handleState")
        // 判断是否已处理
        if (isHandle) {
            llHandle.setVisible(true)
            llNoHandle.setVisible(false)
            when (handleState) {
                "已同意" -> {
                    btHandleApply.isEnabled = false
                    btHandleApply.setBackgroundResource(R.drawable.house_blue_btn)
                    btHandleApply.setTextColor(resources.getColor(R.color.white))
                }
                "已拒绝" -> {
                    btHandleApply.isEnabled = false
                    btHandleApply.setBackgroundResource(R.drawable.shape_background_corner_hint_fill2)
                    btHandleApply.setTextColor(resources.getColor(R.color.text_xdt))
                }
                "已撤回" -> {
                    btHandleApply.isEnabled = false
                    btHandleApply.setBackgroundResource(R.drawable.shape_background_corner_hint_fill2)
                    btHandleApply.setTextColor(resources.getColor(R.color.text_xdt))
                }
            }
        } else {
            llHandle.setVisible(false)
            llNoHandle.setVisible(true)
        }
        btHandleApply.text = handleState
        // 0 学生 2 老师
        if (identityType == 0) {
            llChildren.setVisible(true)
            llTeacher.setVisible(false)
            nameTag.text = "学生"
            nameTag.setBackgroundResource(R.drawable.shape_gradient_green2)
            nameTag.setTextColor(resources.getColor(R.color.xdt_green))
            tvBirthday.text = "生日"
            // 配置学生家长Rv
            parentList = arrayListOf()
            childParentAdapter = AddressBookStudentParentListAdapter(this, parentList)
            rvAddressBookHandleInfo.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvAddressBookHandleInfo.adapter = childParentAdapter
            rvAddressBookHandleInfo.addItemDecoration(
                RecycleViewDivider(
                    this,
                    RecycleViewDivider.VERTICAL,
                    Dp2pxUtils.dp2px(this, 1)
                )
            )
            // 调用获取孩子详情接口
            mPresenter.getChildDetail(classId, chooseUserId, AppPrefsUtils.getInt("userId"))
        } else {
            llChildren.setVisible(false)
            llTeacher.setVisible(true)
            nameTag.text = "老师"
            nameTag.setBackgroundResource(R.drawable.shape_gradient_orange2)
            nameTag.setTextColor(resources.getColor(R.color.xdt_class_list_text_color))
            tvBirthday.text = "电话"
            // 调用获取老师详情接口
            mPresenter.getTeacherDetail(classId, chooseUserId, AppPrefsUtils.getInt("userId"))
        }
    }

    // 孩子详情回调
    override fun getChildDetail(bean: AddressBookChildDetailBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civAddressBookHandleIcon)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civAddressBookHandleIcon)
        }
        // 姓名
        tvAddressBookHandleName.text = bean.realName
        // 性别
        if ("male" == bean.sex) {
            tvAddressBookHandleSex.text = "男"
        } else {
            tvAddressBookHandleSex.text = "女"
        }
        tvAddressBookHandleBirthday.text = bean.birthday
        // 家长信息
        parentList.clear()
        parentList.addAll(bean.patriarchs)
        childParentAdapter.notifyDataSetChanged()
    }

    // 老师详情回调
    override fun getTeacherDetail(bean: AddressBookTeacherDetailBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civAddressBookHandleIcon)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civAddressBookHandleIcon)
        }
        // 姓名
        tvAddressBookHandleName.text = bean.realName
        // 性别
        if ("male" == bean.sex) {
            tvAddressBookHandleSex.text = "男"
        } else {
            tvAddressBookHandleSex.text = "女"
        }
        // 联系方式
        tvAddressBookHandleBirthday.text = bean.telephone
        // 学校
        tvAddressBookHandleEducation.text = bean.schoolName
        // 学历
        tvAddressBookHandleProfession.text = bean.subjectName
    }

    // 保存/拒绝
    override fun approvalResult(msg: String) {
        ToastUtils.showMsg(this,msg)
        finish()
    }
}