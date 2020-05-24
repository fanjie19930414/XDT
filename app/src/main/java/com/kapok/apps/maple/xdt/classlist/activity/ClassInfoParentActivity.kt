package com.kapok.apps.maple.xdt.classlist.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.bean.ClassInfoBean
import com.kapok.apps.maple.xdt.classlist.presenter.ClassInfoParentPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.ClassInfoTeacherPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassInfoParentView
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassInfoTeacherView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.class_info_activity.*
import kotlinx.android.synthetic.main.class_info_parent_activity.*

/**
 * 班级资料家长端
 */
class ClassInfoParentActivity : BaseMVPActivity<ClassInfoParentPresenter>(), ClassInfoParentView {
    private var classId: Int = -1
    private var searchType: Int = 1
    private var userId: Int = -1
    private var studentId: Int = -1
    // 三个点弹窗
    private lateinit var classInfoSettingDialog: CustomCancelBottomDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_info_parent_activity)
        iniView()
        initListener()
    }

    private fun iniView() {
        mPresenter = ClassInfoParentPresenter(this)
        mPresenter.mView = this
        tvClassInfoTitleParent.text = "班级资料"
        // 获取传入的classId searchType
        val intent = intent
        classId = intent.getIntExtra("classId", -1)
        searchType = intent.getIntExtra("searchType", 1)
        userId = AppPrefsUtils.getInt("userId")
        studentId = intent.getIntExtra("studentId", -1)
    }

    private fun initListener() {
        // 返回
        ivClassInfoBackParent.setOnClickListener { finish() }
        // 三个点
        ivClassInfoSettingParent.setOnClickListener {
            // dialog
            classInfoSettingDialog =
                CustomCancelBottomDialog(this@ClassInfoParentActivity, R.style.BottomDialog)
            classInfoSettingDialog.addItem(
                "退出此班级",
                R.color.xdt_exit_text,
                View.OnClickListener {
                    // 退出此班级
                    val confirmDialog = CancelConfirmDialog(
                        this@ClassInfoParentActivity, R.style.BottomDialog
                        , "请确认是否退出此班级", ""
                    )
                    confirmDialog.show()
                    confirmDialog.setOnClickConfirmListener(object :
                        CancelConfirmDialog.ClickConfirmListener {
                        override fun confirm() {
                            // 调用退出班级接口
                            mPresenter.exitClass(
                                classId,
                                studentId,
                                searchType,
                                AppPrefsUtils.getInt("userId")
                            )
                            confirmDialog.dismiss()
                        }
                    })
                    classInfoSettingDialog.dismiss()
                })
            classInfoSettingDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.classUpdate(classId, searchType, userId)
    }

    // 班级资料回调
    override fun getClassInfoTeacherBean(bean: ClassInfoBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, ivClassInfoParent)
        } else {
            Glide.with(this).load(R.mipmap.def_head_class).into(ivClassInfoParent)
        }
        // 班级名称
        tvClassInfoNameParent.text = bean.className
        // 班级年级
        tvClassInfoGradeParent.text = bean.grade
        // 入学年份
        tvClassInfoYearParent.text = bean.startYear.toString() + "年"
        // 所属学校
        tvClassInfoSchoolParent.text = bean.schoolName
        // 班主任
        tvClassInfoHeaderParent.text = bean.headerTeacher
    }

    // 退出班级回调
    override fun exitClass(msg: String) {
        ToastUtils.showMsg(this, msg)
        setResult(Activity.RESULT_OK)
        finish()
    }
}