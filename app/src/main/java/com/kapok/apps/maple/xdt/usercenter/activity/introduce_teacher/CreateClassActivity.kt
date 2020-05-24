package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kapok.apps.maple.xdt.usercenter.presenter.CreateClassPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.CreateClassView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.custom.CustomBottomDialog
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_create_class.*
import kotlinx.android.synthetic.main.activity_teacher_edit_info.*

/**
 *  创建班级页
 *  fanjie
 */
class CreateClassActivity : BaseMVPActivity<CreateClassPresenter>(), CreateClassView {
    private var hasSchoolName: Boolean = false
    private var hasGrade: Boolean = false
    private var hasSchoolYear: Boolean = false
    private var hasClassName: Boolean = false
    // 底部弹窗
    private lateinit var bottomSchoolYearDialog: CustomBottomDialog
    // 选中的学校
    private lateinit var selectSchool: String
    private var selectSchoolId: Int = -1
    // 选中的年级Id / 年级
    private lateinit var selectGradeName: String
    private var selectGradeId: Int = -1
    // 入学年份列表
    private lateinit var schoolYearList: ArrayList<String>
    // 选中的入学年份
    private var selectJoinYear: Int = -1
    // 返回Code
    private val schoolRequestCode = 100
    private val gradeRequestCode = 104


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_class)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = CreateClassPresenter(this@CreateClassActivity)
        mPresenter.mView = this
        // 获取上级页面选中的学校
        val intent = intent
        selectSchool = intent.getStringExtra("selectSchool")
        selectSchoolId = intent.getIntExtra("selectSchoolId", -1)
        initData()
    }

    private fun initData() {
        // 传入的学校
        if (selectSchool.isNotEmpty() && selectSchoolId != -1) {
            tv_relative_school.text = selectSchool
            hasSchoolName = true
            bt_create_class.isEnabled = hasSchoolName && hasGrade && hasSchoolYear && hasClassName
        }
        // 配置入学年份列表
        schoolYearList = arrayListOf()
        for (i in 10..20) {
            schoolYearList.add("20" + i.toString() + "级")
        }
    }

    private fun initListener() {
        // 学校选择监听
        rl_relative_school.setOnClickListener {
            val intent = Intent()
            intent.putExtra("selectSchoolName", selectSchool)
            intent.putExtra("selectSchoolId", selectSchoolId)
            intent.setClass(this@CreateClassActivity, SchoolLocationActivity::class.java)
            startActivityForResult(intent, schoolRequestCode)
        }
        // 班级选择监听
        rl_class_grade.setOnClickListener {
            val intent = Intent()
            if (hasGrade) {
                intent.putExtra("selectGrade", selectGradeName)
                intent.putExtra("selectGradeId", selectGradeId)
            } else {
                intent.putExtra("selectGrade", "")
                intent.putExtra("selectGradeId", selectGradeId)
            }
            intent.setClass(this, ChooseGradeActivity::class.java)
            startActivityForResult(intent, gradeRequestCode)
        }
        // 入学年份监听
        rl_join_school_year.setOnClickListener {
            bottomSchoolYearDialog = CustomBottomDialog(this@CreateClassActivity, R.style.BottomDialog)
            bottomSchoolYearDialog.setTitle("选择入学年份")
            if (hasSchoolYear) {
                bottomSchoolYearDialog.addItem(schoolYearList, tv_join_school_year.text.toString())
            } else {
                bottomSchoolYearDialog.addItem(schoolYearList, "")
            }
            bottomSchoolYearDialog.show()
            bottomSchoolYearDialog.setOnselectTextListener(object : CustomBottomDialog.SelectTextListener {
                override fun selectText(text: String) {
                    selectJoinYear = text.substring(0, text.length - 1).toInt()
                    tv_join_school_year.text = text
                    hasSchoolYear = true
                    bt_create_class.isEnabled = hasSchoolName && hasGrade && hasSchoolYear && hasClassName
                }
            })
        }
        // 班级名称监听
        et_class_name.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                hasClassName = s.toString().isNotEmpty()
                bt_create_class.isEnabled = hasSchoolName && hasGrade && hasSchoolYear && hasClassName
            }
        })
        // 创建班级监听
        bt_create_class.setOnClickListener {
            val userId = AppPrefsUtils.getInt("userId")
            mPresenter.createClass(
                et_class_name.text.toString().trim(),
                selectGradeName,
                selectGradeId,
                selectSchoolId,
                selectJoinYear,
                userId
            )
        }
    }

    // 获取班级创建是否成功回调
    override fun createResult(success: String) {
        ToastUtils.showMsg(this, success)
        val intent = Intent(this,MainActivity::class.java)
        // 目前测试 本地写死状态 id 为 1 是老师  2是家长
        intent.putExtra("id",1)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            schoolRequestCode -> {
                selectSchool = data!!.getStringExtra("selectSchool")
                selectSchoolId = data.getIntExtra("selectSchoolId", -1)
                hasSchoolName = selectSchoolId != -1 && selectSchool.isNotEmpty()
                tv_relative_school.text = selectSchool
                bt_create_class.isEnabled = hasSchoolName && hasGrade && hasSchoolYear && hasClassName
            }
            gradeRequestCode -> {
                selectGradeName = data!!.getStringExtra("selectItem").toString()
                selectGradeId = data.getIntExtra("selectItemId", -1)
                hasGrade = selectGradeName.isNotEmpty() && selectGradeId != -1
                tv_class_grade.text = selectGradeName
                bt_create_class.isEnabled = hasSchoolName && hasGrade && hasSchoolYear && hasClassName
            }
        }
    }

}