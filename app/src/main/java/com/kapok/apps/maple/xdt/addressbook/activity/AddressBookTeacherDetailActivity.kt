package com.kapok.apps.maple.xdt.addressbook.activity

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetailBean
import com.kapok.apps.maple.xdt.addressbook.presenter.AddressBookTeacherDetailPresenter
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookTeacherDetailView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.CallPhoneUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_addressbook_teacher_detail.*

/**
 * 通讯录老师详情页面
 */
class AddressBookTeacherDetailActivity: BaseMVPActivity<AddressBookTeacherDetailPresenter>(),AddressBookTeacherDetailView {
    // 传参
    private var classId = -1
    private var teacherId = -1
    private var teacherName = ""
    // 三个点Dialog
    private lateinit var teacherSettingDialog: CustomCancelBottomDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressbook_teacher_detail)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = AddressBookTeacherDetailPresenter(this)
        mPresenter.mView = this
        // 传参
        classId = intent.getIntExtra("classId",-1)
        teacherId = intent.getIntExtra("teacherId" ,-1)
        teacherName =  intent.getStringExtra("teacherName")
        tvAddressBookTeacherTitle.text = teacherName
        // 调用接口
        mPresenter.getTeacherDetail(classId,teacherId,AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 返沪
        ivAddressBookTeacherBack.setOnClickListener { finish() }
        // 三个点
        ivAddressBookTeacherSetting.setOnClickListener {
            teacherSettingDialog =
                CustomCancelBottomDialog(this, R.style.BottomDialog)
            teacherSettingDialog.addItem(
                "移出班级",
                R.color.text_red,
                View.OnClickListener {
                    // 解除绑定
                    val confirmDialog = CancelConfirmDialog(
                        this,
                        R.style.BottomDialog,
                        "确认将该老师移出班级吗？",
                        ""
                    )
                    confirmDialog.setConfirmContent("移出班级")
                    confirmDialog.show()
                    confirmDialog.setOnClickConfirmListener(object :
                        CancelConfirmDialog.ClickConfirmListener {
                        override fun confirm() {
                            // 移除班级接口
                            mPresenter.detachClass(classId,teacherId,AppPrefsUtils.getInt("userId"))
                            confirmDialog.dismiss()
                        }
                    })
                    teacherSettingDialog.dismiss()
                })
            teacherSettingDialog.show()
        }
        // 打电话
        tvAddressBookTeacherPhone.setOnClickListener {
            if (tvAddressBookTeacherPhone.text.isNotEmpty()) {
                CallPhoneUtils.callPhone(this@AddressBookTeacherDetailActivity,tvAddressBookTeacherPhone.text.toString().trim())
            }
        }
    }

    override fun getTeacherDetail(bean: AddressBookTeacherDetailBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civAddressBookTeacherIcon)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civAddressBookTeacherIcon)
        }
        // 姓名
        tvAddressBookTeacherName.text = bean.realName
        // 性别
        if ("male" == bean.sex) {
            tvAddressBookTeacherSex.text = "男"
        } else {
            tvAddressBookTeacherSex.text = "女"
        }
        // 联系方式
        tvAddressBookTeacherPhone.text = bean.telephone
        // 学校
        tvAddressBookTeacherEducation.text = bean.schoolName
        // 学历
        tvAddressBookTeacherProfession.text = bean.subjectName
    }

    override fun detachClass(msg: String) {
        ToastUtils.showMsg(this,msg)
        finish()
    }
}