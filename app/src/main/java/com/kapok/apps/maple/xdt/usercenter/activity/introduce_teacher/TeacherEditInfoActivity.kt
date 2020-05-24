package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.usercenter.presenter.EditInfoPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.TeacherEditInfoPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.EditInfoView
import com.kapok.apps.maple.xdt.usercenter.presenter.view.TeacherEditInfoView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.custom.CustomBottomDialog
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_editinfo.*
import kotlinx.android.synthetic.main.activity_teacher_edit_info.*

/**
 * 老师完善信息页(此页面BaseView需要替换)
 * fanjie
 */
class TeacherEditInfoActivity : BaseMVPActivity<TeacherEditInfoPresenter>(), TeacherEditInfoView {
    private var hasTeacherName: Boolean = false
    private var hasTeacherSex: Boolean = false
    private var hasSchoolName: Boolean = false
    private var hasSubject: Boolean = false
    // 底部弹窗
    private lateinit var bottomTeacherDialog: CustomBottomDialog
    // 老师学科 / 性别
    private val subjectList: ArrayList<SubjectListBean> = arrayListOf()
    private var subjectListString = arrayListOf<String>()
    private val sexList: ArrayList<String> = arrayListOf("男", "女")
    // 选中学科的ID
    private var subjectId: Int = -1
    // 学校RequestCode
    private val schoolRequestCode = 100
    // 返回的学校id
    private var schoolId: Int = -1
    private var selectSchool: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_edit_info)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = TeacherEditInfoPresenter(this)
        mPresenter.mView = this
        // 判断该用户是否已经含有用户名字了
        if (AppPrefsUtils.getString("userName").isNotEmpty()) {
            et_editinfo_teachername.text = SpannableStringBuilder(AppPrefsUtils.getString("userName"))
            hasTeacherName = et_editinfo_teachername.toString().isNotEmpty()
            bt_editinfo_teacher.text = "保存，并切换为老师身份"
        }
    }

    private fun initData() {
        // 获取学科列表
        mPresenter.getSubjectList("","",schoolId.toString())
    }

    private fun initListener() {
        // 老师姓名监听
        et_editinfo_teachername.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                hasTeacherName = s.toString().isNotEmpty()
                bt_editinfo_teacher.isEnabled = hasTeacherName && hasTeacherSex && hasSchoolName && hasSubject
            }
        })
        // 老师性别弹窗
        rl_editinfo_teachersex.setOnClickListener {
            bottomTeacherDialog = CustomBottomDialog(this@TeacherEditInfoActivity, R.style.BottomDialog)
            bottomTeacherDialog.setTitle("选择性别")
            if (hasTeacherSex) {
                bottomTeacherDialog.addItem(sexList, tv_editinfo_teachersex.text.toString())
            } else {
                bottomTeacherDialog.addItem(sexList, "")
            }
            bottomTeacherDialog.show()
            bottomTeacherDialog.setOnselectTextListener(object : CustomBottomDialog.SelectTextListener {
                override fun selectText(text: String) {
                    tv_editinfo_teachersex.text = text
                    hasTeacherSex = true
                    bt_editinfo_teacher.isEnabled = hasTeacherName && hasTeacherSex && hasSchoolName && hasSubject
                }
            })
        }
        // 学校监听
        rl_editinfo_school.setOnClickListener {
            val intent = Intent()
            intent.putExtra("selectSchoolName",selectSchool)
            intent.putExtra("selectSchoolId",schoolId)
            intent.setClass(this@TeacherEditInfoActivity,SchoolLocationActivity::class.java)
            startActivityForResult(intent, schoolRequestCode)
        }
        // 学科监听
        rl_editinfo_subject.setOnClickListener {
            if (schoolId == -1) {
                ToastUtils.showMsg(this,"请先选择学校")
            } else {
                bottomTeacherDialog = CustomBottomDialog(this@TeacherEditInfoActivity, R.style.BottomDialog)
                bottomTeacherDialog.setTitle("选择学科")
                if (hasSubject) {
                    bottomTeacherDialog.addItem(subjectListString, tv_editinfo_subject.text.toString())
                } else {
                    bottomTeacherDialog.addItem(subjectListString, "")
                }
                bottomTeacherDialog.show()
                bottomTeacherDialog.setOnselectTextListener(object : CustomBottomDialog.SelectTextListener {
                    override fun selectText(text: String) {
                        tv_editinfo_subject.text = text
                        for (item in subjectList) {
                            if (text == item.subjectName) {
                                subjectId = item.subjectId
                            }
                        }
                        hasSubject = true
                        bt_editinfo_teacher.isEnabled = hasTeacherName && hasTeacherSex && hasSchoolName && hasSubject
                    }
                })
            }
        }
        // 保存信息
        bt_editinfo_teacher.setOnClickListener {
            // 调用保存信息接口
            val sexText = if (tv_editinfo_teachersex.text.contains("男")) {
                "male"
            } else {
                "famale"
            }
            val userId = AppPrefsUtils.getInt("userId")
            mPresenter.saveInfo(
                et_editinfo_teachername.text.toString(),
                schoolId,
                sexText,
                subjectId,
                userId
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            schoolRequestCode -> {
                selectSchool = data!!.getStringExtra("selectSchool")
                schoolId = data.getIntExtra("selectSchoolId", -1)
                hasSchoolName = schoolId != -1 && !selectSchool.isNullOrEmpty()
                tv_editinfo_school.text = selectSchool
                initData()
                bt_editinfo_teacher.isEnabled = hasTeacherName && hasTeacherSex && hasSchoolName && hasSubject
            }
        }
    }

    // 保存信息回调
    override fun saveSuccessful(boolean: Boolean) {
        if (boolean) {
            // 保存身份
            AppPrefsUtils.putString("identity","2")
            BaseUserInfo.identity = 2
            AppPrefsUtils.putString("userName", et_editinfo_teachername.text.toString())
            // 将创建的老师 所属学校记录在本地
            AppPrefsUtils.putInt("selectSchoolId",schoolId)
            AppPrefsUtils.putString("selectSchool",selectSchool)
            val intent = Intent()
            intent.putExtra("selectSchool", selectSchool)
            intent.putExtra("selectSchoolId", schoolId)
            intent.putExtra("teacherName",et_editinfo_teachername.text.toString())
            intent.putExtra("teacherRelation",tv_editinfo_subject.text.toString())
            intent.setClass(this@TeacherEditInfoActivity, AddClassActivity::class.java)
            startActivity(intent)
            AppManager.instance.finishActivity(this)
        }
    }

    // 返回的课程列表List
    override fun getSubjectList(dataList: MutableList<SubjectListBean>?) {
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                subjectList.addAll(dataList)
                for (item in subjectList) {
                    subjectListString.add(item.subjectName)
                }
            }
        }
    }
}