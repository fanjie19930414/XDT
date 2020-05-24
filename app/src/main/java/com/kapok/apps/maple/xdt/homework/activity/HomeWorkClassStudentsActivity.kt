package com.kapok.apps.maple.xdt.homework.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkClassStudentAdapter
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.presenter.HomeWorkClassStudentPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkClassStudentView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_homework_classstudents.*

/**
 * 发布作业选择班级下孩子列表页
 */
@SuppressLint("SetTextI18n")
class HomeWorkClassStudentsActivity : BaseMVPActivity<HomeWorkClassStudentPresenter>(),
    HomeWorkClassStudentView {
    // 从哪里传入的
    private var from = ""
    // 选中班级的Info
    private lateinit var chooseClassInfo: TeacherInClasses
    // 传入的学生列表集合
    private var studentInfoList = arrayListOf<StudentInClasses>()
    // 接口请求下班级下学生列表结合
    private lateinit var studentList: MutableList<StudentInClasses>
    // 班级下学生列表Adapter
    private lateinit var studentAdapter: HomeWorkClassStudentAdapter
    // 全选 还是 单选状态 (-1 没选 0单选 1全选)
    private var selectState = -1
    // 是否有选中的学生
    private var isChooseStudent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_classstudents)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = HomeWorkClassStudentPresenter(this)
        mPresenter.mView = this
        // 获取选中班级Info
        from = intent.getStringExtra("from")
        chooseClassInfo = intent.getParcelableExtra("classInfo")
        studentInfoList = chooseClassInfo.chooseStudentInfo
        // 确定
        headerBarClassStudents.getRightView().setVisible(true)
        headerBarClassStudents.getRightView().text = "确定"
        headerBarClassStudents.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        // Title
        headerBarClassStudents.setTitle(chooseClassInfo.grade + chooseClassInfo.className + "班" + "(" + chooseClassInfo.startYear + "级)")
        // 选中状态
        if (studentInfoList.size > 0) {
            for (item in studentInfoList) {
                if (!item.isChoose) {
                    selectState = 0
                    break
                } else {
                    selectState = 1
                }
            }
        } else {
            selectState = -1
        }
        // 班级名称
        tvClassStudentName.text = chooseClassInfo.grade + chooseClassInfo.className + "班"
        // 已选学生
        haveChooseStudentNum(0)
        selectAllOrSingle()
        // 配置Rv
        studentList = arrayListOf()
        studentAdapter = HomeWorkClassStudentAdapter(this, studentList)
        rvHomeWorkClassStudent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvHomeWorkClassStudent.adapter = studentAdapter
        rvHomeWorkClassStudent.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1),
                resources.getColor(R.color.login_xdt_view_line)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty2, rvHomeWorkClassStudent, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "还没有学生加入班级~"
        studentAdapter.emptyView = emptyView
        // 调用接口
        mPresenter.getStudentInClasses(chooseClassInfo.classId, AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 返回
        headerBarClassStudents.getLeftView().setOnClickListener {
            finish()
        }
        // 确定
        headerBarClassStudents.getRightView().setOnClickListener {
            // 记录选中
            val tempStudentInfoList = arrayListOf<StudentInClasses>()
            tempStudentInfoList.addAll(studentList)
            val intent = Intent()
            if (tempStudentInfoList.size > 0) {
                if (from == "Send") {
                    for (item in tempStudentInfoList) {
                        if (item.isChoose) {
                            isChooseStudent = true
                            break
                        } else {
                            isChooseStudent = false
                        }
                    }
                    if (isChooseStudent) {
                        intent.putExtra("studentInfo",tempStudentInfoList)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    } else {
                        ToastUtils.showMsg(this,"请最少选择1个班级的1名学生")
                    }
                } else {
                    intent.putExtra("studentInfo",tempStudentInfoList)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            } else {
                ToastUtils.showMsg(this,"该班级下还没有学生~")
            }
        }
        // 全选or单选
        ivClassStudent.setOnClickListener {
            if (studentList.size > 0) {
                // 单选状态时候 点击(全部变为全选)
                if (-1 == selectState || 0 == selectState) {
                    selectState = 1
                    for (item in studentList) {
                        item.isChoose = true
                    }
                    ivClassStudent.setImageResource(R.mipmap.chk_box_on)
                    studentAdapter.notifyDataSetChanged()
                } else {
                    selectState = -1
                    for (item in studentList) {
                        item.isChoose = false
                    }
                    ivClassStudent.setImageResource(R.mipmap.chk_box_off)
                    studentAdapter.notifyDataSetChanged()
                }
            }
            haveChooseStudentNum(1)
        }
        // 选中学生点击
        studentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                var isAllSelcted = false
                studentList[position].isChoose = !studentList[position].isChoose
                for (item in studentList) {
                    if (!item.isChoose) {
                        selectState = 0
                        break
                    } else {
                        selectState = 1
                    }
                }
                studentAdapter.notifyDataSetChanged()
                haveChooseStudentNum(1)
                selectAllOrSingle()
            }
    }

    // 单选 全选的展示
    private fun selectAllOrSingle() {
        when(selectState) {
            -1 -> {
                ivClassStudent.setImageResource(R.mipmap.chk_box_off)
            }
            0 -> {
                ivClassStudent.setImageResource(R.mipmap.icon_check_box)
            }
            1 -> {
                ivClassStudent.setImageResource(R.mipmap.chk_box_on)
            }
        }
    }

    // 已选学生数量 (0 为传入的列表   1 为接口列表)
    private fun haveChooseStudentNum(type: Int) {
        var count = 0
        if (type == 0) {
            for (item in studentInfoList) {
                if (item.isChoose) {
                    count += 1
                }
            }
            if (count == studentInfoList.size) {
                selectState = 1
            }
        } else {
            for (item in studentList) {
                if (item.isChoose) {
                    count += 1
                }
            }
            if (count == studentList.size) {
                selectState = 1
            }
        }
        if (count == 0) {
            selectState = -1
        }
        tvClassStudentNum.text = "已选学生" + count + "人"
    }

    // 获取班级下学生列表
    override fun getStudentInClasses(bean: MutableList<StudentInClasses>?) {
        studentList.clear()
        // 判断是否已经有值记录了
        if (studentInfoList.size > 0) {
            studentList.addAll(studentInfoList)
        } else {
            if (bean != null && bean.size > 0) {
                studentList.addAll(bean)
            }
        }
        studentAdapter.notifyDataSetChanged()
    }
}