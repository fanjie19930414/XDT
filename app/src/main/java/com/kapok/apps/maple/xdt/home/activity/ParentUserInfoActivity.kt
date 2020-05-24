package com.kapok.apps.maple.xdt.home.activity

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.presenter.ParentUserInfoPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.ParentUserInfoView
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomBottomDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_editinfo.*
import kotlinx.android.synthetic.main.activity_parent_userinfo.*

/**
 * 孩子亲属资料页面
 * fanjie
 */
class ParentUserInfoActivity : BaseMVPActivity<ParentUserInfoPresenter>(), ParentUserInfoView {
    // 亲属Id
    private var parentUserId = -1
    // 孩子Id
    private var studentId = -1
    // 亲属设置弹窗
    private lateinit var parentRelationSettingDialog: CustomCancelBottomDialog
    // 家长和孩子的关系
    private var relationList: MutableList<RelationListBean> = arrayListOf()
    // 家长和孩子关系的id
    private var relationId: Int = -1
    private var hasParentRelation = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_userinfo)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = ParentUserInfoPresenter(this)
        mPresenter.mView = this
        // 获取传入的userId
        parentUserId = intent.getIntExtra("parentUserId", -1)
        studentId = intent.getIntExtra("studentId", -1)
        // 调用获取用户信息接口
        mPresenter.getUserInfo(parentUserId, 1)
        // 调用获取关系列表
        mPresenter.getRelationList()
    }

    private fun initListener() {
        // 返回
        mLeftIvParentInfo.setOnClickListener { finish() }
        // 三个点
        mRightIvParentInfo.setOnClickListener {
            parentRelationSettingDialog = CustomCancelBottomDialog(this@ParentUserInfoActivity, R.style.BottomDialog)
            parentRelationSettingDialog.addItem(
                "修改与孩子的关系",
                R.color.text_xdt,
                View.OnClickListener {
                    val relationListString = arrayListOf<String>()
                    val bottomDialog = CustomBottomDialog(this@ParentUserInfoActivity, R.style.BottomDialog)
                    bottomDialog.setTitle("选择关系")
                    if (hasParentRelation) {
                        for (item in relationList) {
                            relationListString.add(item.relationName)
                        }
                        bottomDialog.addItem(relationListString, tv_editinfo_relation.text.toString())
                    } else {
                        for (item in relationList) {
                            relationListString.add(item.relationName)
                        }
                        bottomDialog.addItem(relationListString, "")
                    }
                    bottomDialog.show()
                    bottomDialog.setOnselectTextListener(object : CustomBottomDialog.SelectTextListener {
                        override fun selectText(text: String) {
                            for (item in relationList) {
                                if (text == item.relationName) {
                                    // 记录选中的关系Id
                                    relationId = item.relationId
                                }
                            }
                            hasParentRelation = true
                        }
                    })
                    parentRelationSettingDialog.dismiss()
                })
            // 判断是否是创建者 再判断点击的条目是否是自己（目前值判断了是否是自己）
            // 点击其他家属 （增加解除绑定选项）
            if (BaseUserInfo.userId != parentUserId) {
                parentRelationSettingDialog.addItem(
                    "解除绑定",
                    R.color.text_red,
                    View.OnClickListener {
                        // 解除绑定
                        val confirmDialog = CancelConfirmDialog(
                            this@ParentUserInfoActivity, R.style.BottomDialog, "解除绑定后将无法收到孩子的消息\n" +
                                    "确认解除绑定吗？", ""
                        )
                        confirmDialog.setConfirmContent("解绑")
                        confirmDialog.show()
                        confirmDialog.setOnClickConfirmListener(object :
                            CancelConfirmDialog.ClickConfirmListener {
                            override fun confirm() {
                                // 解除绑定接口
                                mPresenter.unBindChild(parentUserId, studentId)
                                confirmDialog.dismiss()
                            }
                        })
                        parentRelationSettingDialog.dismiss()
                    })
            }
            parentRelationSettingDialog.show()
        }
    }

    override fun getUserInfoBean(bean: UserInfoBean) {
        // 将接口返回的信息存到本地SP中
        AppPrefsUtils.putInt("userId", bean.userId)
        BaseUserInfo.userId = bean.userId
        if (bean.identityType != null) {
            AppPrefsUtils.putString("identity",bean.identityType.toString())
            BaseUserInfo.identity = bean.identityType
        } else {
            AppPrefsUtils.putString("identity","")
        }
        if (bean.realName.isNotEmpty()) {
            AppPrefsUtils.putString("userName", bean.realName)
            BaseUserInfo.userName = bean.realName
        }
        // 对应关系
        mTitleTvParentInfo.text = bean.realName
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civIconParentRelation)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civIconParentRelation)
        }
        // 姓名
        tvParentRelationName.text = bean.realName
        // 联系方式
        tvParentRelationPhone.text = bean.telephone
        // 性别
        if ("male" == bean.sex) {
            tvParentRelationInfoSex.text = "男"
        } else {
            tvParentRelationInfoSex.text = "女"
        }
        // 学历
        tvParentRelationEducation.text = bean.education
        // 职业
        tvParentRelationPerfession.text = bean.job
    }

    // 获取家属关系列表
    override fun getRelationList(dataList: MutableList<RelationListBean>?) {
        relationList.clear()
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                relationList.addAll(dataList)
            }
        }
    }

    // 解除 家属和孩子绑定的回调
    override fun unBindChild(msg: String) {
        ToastUtils.showMsg(this@ParentUserInfoActivity, msg)
    }
}