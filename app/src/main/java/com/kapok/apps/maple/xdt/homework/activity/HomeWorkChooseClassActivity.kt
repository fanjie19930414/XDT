package com.kapok.apps.maple.xdt.homework.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkChooseClassAdapter
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.presenter.HomeWorkChooseClassPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkChooseClassView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_homework_choossclass.*
import java.util.ArrayList

/**
 * 发布作业选择班级页
 */
class HomeWorkChooseClassActivity : BaseMVPActivity<HomeWorkChooseClassPresenter>(),
    HomeWorkChooseClassView {
    // 发作页页面传入的班级信息列表
    private lateinit var classInfoList: ArrayList<TeacherInClasses>
    // 班级列表集合
    private lateinit var classList: MutableList<TeacherInClasses>
    // 班级列表Adapter
    private lateinit var classAdapter: HomeWorkChooseClassAdapter
    // 是否选中
    private var isChoose = false
    // 跳转班级下学生页面Code
    private val chooseStudentCode = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_choossclass)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = HomeWorkChooseClassPresenter(this)
        mPresenter.mView = this
        // 确定
        headerBarChooseClass.getRightView().setVisible(true)
        headerBarChooseClass.getRightView().text = "确定"
        headerBarChooseClass.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        // 传入的班级列表信息
        classInfoList = intent.getParcelableArrayListExtra<TeacherInClasses>("classListInfo")
        // 配置Rv
        classList = arrayListOf()
        classAdapter = HomeWorkChooseClassAdapter(this, classList)
        rvHomeWorkChooseClass.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvHomeWorkChooseClass.adapter = classAdapter
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty2, rvHomeWorkChooseClass, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "您还没有加入班级~"
        classAdapter.emptyView = emptyView
        // 调用接口
        mPresenter.getTeacherInClasses(AppPrefsUtils.getInt("userId").toString())
    }

    private fun initListener() {
        // 返回
        headerBarChooseClass.getLeftView().setOnClickListener {
            finish()
        }
        // 确定
        headerBarChooseClass.getRightView().setOnClickListener {
            for (item in classList) {
                if (item.isChoose) {
                    isChoose = item.chooseStudentNum != 0
                    if (!isChoose) {
                        break
                    }
                }
            }
            if (isChoose) {
                // 班级和学生列表集合
                val classInfoList = arrayListOf<TeacherInClasses>()
                classInfoList.addAll(classList)
                val intent = Intent()
                intent.putExtra("classInfo",classInfoList)
                setResult(Activity.RESULT_OK,intent)
                finish()
            } else {
                ToastUtils.showMsg(this,"请最少选择1个班级的1名学生")
            }
        }
//        // 选中班级点击事件 (暂时取消多选)
//        classAdapter.onItemChildClickListener =
//            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
//                classList[position].isChoose = !classList[position].isChoose
//                classAdapter.notifyDataSetChanged()
//            }
        // 班级列表点击事件
        classAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                for (item in classList) {
                    item.isChoose = false
                }
                classList[position].isChoose = true
                val intent = Intent(
                    this@HomeWorkChooseClassActivity,
                    HomeWorkClassStudentsActivity::class.java
                )
                intent.putExtra("classInfo", classList[position])
                intent.putExtra("from","Class")
                startActivityForResult(intent, chooseStudentCode)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                chooseStudentCode -> {
                    if (data != null) {
                        val studentListInfo: ArrayList<StudentInClasses> = data.getParcelableArrayListExtra<StudentInClasses>("studentInfo")
                        // 数量
                        var count = 0
                        for (item in studentListInfo) {
                            if (item.isChoose) {
                                count += 1
                            }
                        }
                        // 赋值
                        for (item in classList) {
                            if (item.classId == studentListInfo[0].classId) {
                                item.chooseStudentNum = count
                                item.chooseStudentInfo = studentListInfo
                            }
                        }
                        classAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    // 获取老师所在班级
    override fun getTeacherInClasses(bean: MutableList<TeacherInClasses>?) {
        classList.clear()
        // 判断是否有值
        if (classInfoList.size > 0) {
            classList.addAll(classInfoList)
        } else {
            if (bean != null && bean.size > 0) {
                classList.addAll(bean)
                for (item in classList) {
                    item.chooseStudentInfo = arrayListOf()
                    item.chooseTeacherInfo = arrayListOf()
                }
            }
        }
        classAdapter.notifyDataSetChanged()
    }
}