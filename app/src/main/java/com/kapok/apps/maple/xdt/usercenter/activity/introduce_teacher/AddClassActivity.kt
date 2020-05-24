package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.content.Intent
import android.os.Bundle
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_student.JoinClassActivity
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.activity_add_class.*

/**
 *  添加班级页
 *  fanjie
 */
class AddClassActivity : BaseActivity() {
    private lateinit var selectSchool: String
    private var selectSchoolId: Int = -1
    private lateinit var teacherName : String
    private lateinit var teacherRelation : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_class)
        HeaderBarAddClass.getLeftView().setVisible(false)
        initListener()
        initData()
    }

    private fun initData() {
        val intent = intent
        selectSchool = intent.getStringExtra("selectSchool")
        selectSchoolId = intent.getIntExtra("selectSchoolId", -1)
        teacherName = intent.getStringExtra("teacherName")
        teacherRelation = intent.getStringExtra("teacherRelation")
    }

    private fun initListener() {
        // 创建班级
        rl_create_class.setOnClickListener {
            // 跳转到创建班级页
            val intent = Intent()
            intent.putExtra("selectSchool", selectSchool)
            intent.putExtra("selectSchoolId", selectSchoolId)
            intent.setClass(this, CreateClassActivity::class.java)
            startActivity(intent)
        }
        // 加入班级
        rl_join_class.setOnClickListener {
            // 跳转到加入班级页  老师 申请加入 传code 1
            val intent = Intent()
            intent.putExtra("code", 1)
            intent.putExtra("childName",teacherName)
            intent.putExtra("relation", teacherRelation)
            intent.putExtra("fromType","")
            intent.setClass(this@AddClassActivity, JoinClassActivity::class.java)
            startActivity(intent)
        }
        // 暂不加入
        rl_exit_class.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            // 目前测试 本地写死状态 id 为 1 是老师  2是家长
            intent.putExtra("id",1)
            startActivity(intent)
            AppManager.instance.finishActivity(this)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        // 目前测试 本地写死状态 id 为 1 是老师  2是家长
        intent.putExtra("id",1)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
    }
}