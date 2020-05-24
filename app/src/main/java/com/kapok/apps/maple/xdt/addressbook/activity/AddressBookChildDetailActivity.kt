package com.kapok.apps.maple.xdt.addressbook.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.adapter.AddressBookStudentParentListAdapter
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildDetailBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildParentBean
import com.kapok.apps.maple.xdt.addressbook.presenter.AddressBookDetailChildPresenter
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookChildDetailView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.*
import kotlinx.android.synthetic.main.activity_addressbook_child_detail.*

/**
 * 通讯录学生详情页
 */
class AddressBookChildDetailActivity : BaseMVPActivity<AddressBookDetailChildPresenter>(),
    AddressBookChildDetailView {
    // 传参
    private var classId: Int = -1
    private var studentId: Int = -1
    private var isHeaderTeacher: Boolean = false
    private var studentName: String = ""
    // 学生家长列表
    private lateinit var parentList: MutableList<AddressBookChildParentBean>
    private lateinit var childParentAdapter: AddressBookStudentParentListAdapter
    // 三个点
    private lateinit var childrenSettingDialog: CustomCancelBottomDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressbook_child_detail)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = AddressBookDetailChildPresenter(this)
        mPresenter.mView = this
        // 传参
        classId = intent.getIntExtra("classId", -1)
        studentId = intent.getIntExtra("studentId", -1)
        studentName = intent.getStringExtra("studentName")
        isHeaderTeacher = intent.getBooleanExtra("isHeaderTeacher", false)
        tvAddressBookChildTitle.text = studentName
        if (isHeaderTeacher) {
            ivAddressBookChildSetting.setVisible(true)
        } else {
            ivAddressBookChildSetting.setVisible(false)
        }
        // 配置学生家长Rv
        parentList = arrayListOf()
        childParentAdapter = AddressBookStudentParentListAdapter(this, parentList)
        rvAddressBookParentInfo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvAddressBookParentInfo.adapter = childParentAdapter
        rvAddressBookParentInfo.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
        // 获取学生详情接口
        mPresenter.getChildDetail(classId, studentId, AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 返回
        ivAddressBookChildBack.setOnClickListener { finish() }
        // 移除班级
        ivAddressBookChildSetting.setOnClickListener {
            childrenSettingDialog =
                CustomCancelBottomDialog(this, R.style.BottomDialog)
            childrenSettingDialog.addItem(
                "移出班级",
                R.color.text_red,
                View.OnClickListener {
                    // 解除绑定
                    val confirmDialog = CancelConfirmDialog(
                        this,
                        R.style.BottomDialog,
                        "确认将孩子移出班级吗？",
                        ""
                    )
                    confirmDialog.setConfirmContent("移出班级")
                    confirmDialog.show()
                    confirmDialog.setOnClickConfirmListener(object :
                        CancelConfirmDialog.ClickConfirmListener {
                        override fun confirm() {
                            // 移除班级接口
                            mPresenter.detachClass(classId,studentId,AppPrefsUtils.getInt("userId"))
                            confirmDialog.dismiss()
                        }
                    })
                    childrenSettingDialog.dismiss()
                })
            childrenSettingDialog.show()
        }
        // 家长信息
        childParentAdapter.setOnItemClickListener { _, _, position ->
            val intent = Intent(
                this@AddressBookChildDetailActivity,
                AddressBookParentDetailActivity::class.java
            )
            intent.putExtra("classId", classId)
            intent.putExtra("parentId", parentList[position].userId)
            intent.putExtra("studentName", studentName)
            intent.putExtra("relationShip", parentList[position].relation)
            startActivity(intent)
        }
        // 拨打家长电话
        childParentAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ivAddressBookParentDetail -> {
                    CallPhoneUtils.callPhone(this,parentList[position].telephone)
                }
            }
        }
    }

    override fun getChildDetail(bean: AddressBookChildDetailBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civAddressBookChildIcon)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civAddressBookChildIcon)
        }
        // 姓名
        tvAddressBookChildName.text = bean.realName
        // 性别
        if ("male" == bean.sex) {
            tvAddressBookChildSex.text = "男"
        } else {
            tvAddressBookChildSex.text = "女"
        }
        tvAddressBookChildBirthday.text = bean.birthday
        // 家长信息
        parentList.clear()
        parentList.addAll(bean.patriarchs)
        childParentAdapter.notifyDataSetChanged()
    }

    // 移除班级接口回到
    override fun detachClass(msg: String) {
        ToastUtils.showMsg(this,msg)
        finish()
    }
}