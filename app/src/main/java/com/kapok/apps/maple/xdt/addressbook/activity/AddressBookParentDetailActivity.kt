package com.kapok.apps.maple.xdt.addressbook.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookParentDetailBean
import com.kapok.apps.maple.xdt.addressbook.presenter.AddressBookDetailParentPresenter
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookParentDetailView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.CallPhoneUtils
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_addressbook_parent_detail.*

/**
 * 通讯录家长详情页
 */
@SuppressLint("SetTextI18n")
class AddressBookParentDetailActivity : BaseMVPActivity<AddressBookDetailParentPresenter>(),
    AddressBookParentDetailView {
    // 传参
    private var classId: Int = -1
    private var patriarchId: Int = -1
    private var studentName: String = ""
    private var relationShip: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressbook_parent_detail)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = AddressBookDetailParentPresenter(this)
        mPresenter.mView = this
        // 传参
        classId = intent.getIntExtra("classId", -1)
        patriarchId = intent.getIntExtra("parentId", -1)
        studentName = intent.getStringExtra("studentName")
        relationShip = intent.getStringExtra("relationShip")
        tvAddressBookParentTitle.text = studentName + relationShip
        // 获取家长详情接口
        mPresenter.getParentDetail(classId, patriarchId, AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 返回
        ivAddressBookParentBack.setOnClickListener { finish() }
        // 打电话
        tvAddressBookParentPhone.setOnClickListener {
            if (tvAddressBookParentPhone.text.isNotEmpty()) {
                CallPhoneUtils.callPhone(this@AddressBookParentDetailActivity,tvAddressBookParentPhone.text.toString().trim())
            }
        }
    }

    // 获取家长详情回调
    override fun getParentDetail(bean: AddressBookParentDetailBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civAddressBookParentIcon)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civAddressBookParentIcon)
        }
        // 姓名
        tvAddressBookParentName.text = bean.realName
        // 联系方式
        tvAddressBookParentPhone.text = bean.telephone
        // 性别
        if ("male" == bean.sex) {
            tvAddressBookParentSex.text = "男"
        } else {
            tvAddressBookParentSex.text = "女"
        }
        // 学历
        tvAddressBookParentEducation.text = bean.education
        // 职业
        tvAddressBookParentProfession.text = bean.subjectName
        // 权限
        tvAddressBookParentPower.text = "家长"
    }
}