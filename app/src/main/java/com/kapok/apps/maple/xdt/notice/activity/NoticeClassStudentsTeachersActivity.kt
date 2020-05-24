package com.kapok.apps.maple.xdt.notice.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.HomeWorkClassStudentAdapter
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.notice.adapter.NoticeClassTeacherAdapter
import com.kapok.apps.maple.xdt.notice.bean.TeacherInClassesBean
import com.kapok.apps.maple.xdt.notice.presenter.NoticeClassStudentsTeachersPresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.NoticeClassStudentsTeachersView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_notice_classstudentsteachers.*

/**
 * 发布作业选择班级下孩子列表页
 */
@SuppressLint("SetTextI18n")
class NoticeClassStudentsTeachersActivity : BaseMVPActivity<NoticeClassStudentsTeachersPresenter>(),
    NoticeClassStudentsTeachersView {
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

    // 传入的家长列表集合
    private var teacherInfoList = arrayListOf<TeacherInClassesBean>()
    // 接口请求下班级老师列表集合
    private lateinit var teacherList: MutableList<TeacherInClassesBean>
    // 班级下老师列表Adapter
    private lateinit var teacherAdapter: NoticeClassTeacherAdapter

    // 全选 还是 单选状态 (-1未选中 0单选 1全选) 学生
    private var selectStateStudent = 0
    // 全选 还是 单选状态 (-1 未选中 0单选 1全选) 老师
    private var selectStateTeacher = 0
    // 是否有选中的学生
    private var isChooseStudent = false
    // 选中学生的数量
    private var selectStudentCount = 0
    // 选中老师的数量
    private var selectTeacherCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_classstudentsteachers)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = NoticeClassStudentsTeachersPresenter(this)
        mPresenter.mView = this

        // 获取选中班级Info
        from = intent.getStringExtra("from")
        chooseClassInfo = intent.getParcelableExtra("classInfo")
        studentInfoList = chooseClassInfo.chooseStudentInfo
        teacherInfoList = chooseClassInfo.chooseTeacherInfo

        // 确定
        headerBarClassStudents.getRightView().setVisible(true)
        headerBarClassStudents.getRightView().text = "确定"
        headerBarClassStudents.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        // Title
        headerBarClassStudents.setTitle(chooseClassInfo.grade + chooseClassInfo.className + "班" + "(" + chooseClassInfo.startYear + "级)")
        if (studentInfoList.size > 0) {
            // 选中状态
            for (item in studentInfoList) {
                if (!item.isChoose) {
                    selectStateStudent = 0
                    break
                } else {
                    selectStateStudent = 1
                }
            }
        } else {
            selectStateStudent = -1
        }
        if (teacherInfoList.size > 0) {
            for (item in teacherInfoList) {
                if (!item.isChoose) {
                    selectStateTeacher = 0
                    break
                } else {
                    selectStateTeacher = 1
                }
            }
        } else {
            selectStateTeacher = -1
        }


        // 判断是否全选
        selectAllOrSingleStudent()
        selectAllOrSingleTeacher()

        // 已选学生/老师
        selectStudentCount = haveChooseStudentNum(0)
        selectTeacherCount = haveChooseTeacherNum(0)

        // 配置学生Rv
        studentList = arrayListOf()
        studentAdapter = HomeWorkClassStudentAdapter(this, studentList)
        rvNoticeClassStudent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvNoticeClassStudent.adapter = studentAdapter
        rvNoticeClassStudent.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1),
                resources.getColor(R.color.login_xdt_view_line)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty2, rvNoticeClassStudent, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "还没有学生加入班级~"
        studentAdapter.emptyView = emptyView

        // 配置老师Rv
        teacherList = arrayListOf()
        teacherAdapter = NoticeClassTeacherAdapter(this,teacherList)
        rvNoticeClassTeacher.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvNoticeClassTeacher.adapter = teacherAdapter
        rvNoticeClassTeacher.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1),
                resources.getColor(R.color.login_xdt_view_line)
            )
        )
        // emptyView
        val emptyViewTeacher = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty2, rvNoticeClassTeacher, false)
        emptyViewTeacher.findViewById<TextView>(R.id.tvEmptyContent).text = "还没有老师加入班级~"
        teacherAdapter.emptyView = emptyViewTeacher

        // 调用接口
        mPresenter.getStudentInClasses(chooseClassInfo.classId, AppPrefsUtils.getInt("userId"))
        mPresenter.getTeacherInClasses(chooseClassInfo.classId)
    }

    private fun initListener() {
        // 返回
        headerBarClassStudents.getLeftView().setOnClickListener {
            finish()
        }
        // 确定
        headerBarClassStudents.getRightView().setOnClickListener {
            // 记录选中学生
            val tempStudentInfoList = arrayListOf<StudentInClasses>()
            tempStudentInfoList.addAll(studentList)
            // 记录选中老师
            val tempTeacherInfoList = arrayListOf<TeacherInClassesBean>()
            tempTeacherInfoList.addAll(teacherList)
            // setResult
            val intent = Intent()
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
                intent.putExtra("teacherInfo",tempTeacherInfoList)
                setResult(Activity.RESULT_OK,intent)
                finish()
            } else {
                ToastUtils.showMsg(this,"请最少选择1个班级的1名学生")
            }
        }

        // 全选or单选 学生
        ivClassStudent.setOnClickListener {
            if (studentList.size > 0) {
                // 单选状态时候 点击(全部变为全选)
                if (-1 == selectStateStudent || 0 == selectStateStudent) {
                    selectStateStudent = 1
                    for (item in studentList) {
                        item.isChoose = true
                    }
                    ivClassStudent.setImageResource(R.mipmap.chk_box_on)
                    studentAdapter.notifyDataSetChanged()
                } else {
                    selectStateStudent = -1
                    for (item in studentList) {
                        item.isChoose = false
                    }
                    ivClassStudent.setImageResource(R.mipmap.chk_box_off)
                    studentAdapter.notifyDataSetChanged()
                }
            }
            selectStudentCount = haveChooseStudentNum(1)
            tvClassStudentName.text = "学生(" + selectStudentCount + "/" + studentList.size + ")"
        }
        // 全选or单选 老师
        ivClassTeacher.setOnClickListener {
            if (teacherList.size > 0) {
                // 单选状态时候 点击(全部变为全选)
                if (-1 == selectStateTeacher || 0 == selectStateTeacher) {
                    selectStateTeacher = 1
                    for (item in teacherList) {
                        item.isChoose = true
                    }
                    ivClassTeacher.setImageResource(R.mipmap.chk_box_on)
                    teacherAdapter.notifyDataSetChanged()
                } else {
                    selectStateTeacher = -1
                    for (item in teacherList) {
                        item.isChoose = false
                    }
                    ivClassTeacher.setImageResource(R.mipmap.chk_box_off)
                    teacherAdapter.notifyDataSetChanged()
                }
            }
            selectTeacherCount = haveChooseTeacherNum(1)
            tvClassTeacherName.text = "老师(" + selectTeacherCount + "/" + teacherList.size + ")"
        }

        // 选中学生点击
        studentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                studentList[position].isChoose = !studentList[position].isChoose
                for (item in studentList) {
                    if (!item.isChoose) {
                        selectStateStudent = 0
                        break
                    } else {
                        selectStateStudent = 1
                    }
                }
                studentAdapter.notifyDataSetChanged()
                selectStudentCount = haveChooseStudentNum(1)
                if (selectStudentCount == 0) {
                    selectStateStudent = -1
                }
                if (selectStudentCount == studentList.size) {
                    selectStateStudent = 1
                }
                selectAllOrSingleStudent()
                tvClassStudentName.text = "学生(" + selectStudentCount + "/" + studentList.size + ")"
            }
        // 选中老师点击
        teacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                teacherList[position].isChoose = !teacherList[position].isChoose
                for (item in teacherList) {
                    if (!item.isChoose) {
                        selectStateTeacher = 0
                        break
                    } else {
                        selectStateTeacher = 1
                    }
                }
                teacherAdapter.notifyDataSetChanged()
                selectTeacherCount = haveChooseTeacherNum(1)
                if (selectTeacherCount == 0) {
                    selectStateTeacher = -1
                }
                if (selectTeacherCount == teacherList.size) {
                    selectStateTeacher = 1
                }
                selectAllOrSingleTeacher()
                tvClassTeacherName.text = "老师(" + selectTeacherCount + "/" + teacherList.size + ")"
            }

        // 学生折叠
        llClassStudent.setOnClickListener {
            if (rvNoticeClassStudent.visibility == View.VISIBLE) {
                ivClassStudentDown.setImageResource(R.mipmap.prev_list_r)
                rvNoticeClassStudent.setVisible(false)
            } else {
                ivClassStudentDown.setImageResource(R.mipmap.prev_list_d)
                rvNoticeClassStudent.setVisible(true)
            }
        }
        // 老师折叠
        llClassTeacher.setOnClickListener {
            if (rvNoticeClassTeacher.visibility == View.VISIBLE) {
                ivClassTeacherRight.setImageResource(R.mipmap.prev_list_r)
                rvNoticeClassTeacher.setVisible(false)
            } else {
                ivClassTeacherRight.setImageResource(R.mipmap.prev_list_d)
                rvNoticeClassTeacher.setVisible(true)
            }
        }
    }

    // 单选 全选的展示学生
    private fun selectAllOrSingleStudent() {
        when(selectStateStudent) {
            -1 -> {
                ivClassStudent.setImageResource(R.mipmap.chk_box_off)
            }
            0 -> {
                ivClassStudent.setImageResource(R.mipmap.icon_check_box)
            }
            1 -> {
                ivClassStudent.setImageResource(R.mipmap.icon_check_box)
            }
        }
    }

    // 单选 全选的展示老师
    private fun selectAllOrSingleTeacher() {
        when(selectStateTeacher) {
            -1 -> {
                ivClassTeacher.setImageResource(R.mipmap.chk_box_off)
            }
            0 -> {
                ivClassTeacher.setImageResource(R.mipmap.icon_check_box)
            }
            1 -> {
                ivClassTeacher.setImageResource(R.mipmap.icon_check_box)
            }
        }
    }

    // 已选学生数量 (0 为传入的列表   1 为接口列表)
    private fun haveChooseStudentNum(type: Int): Int{
        var count = 0
        if (type == 0) {
            for (item in studentInfoList) {
                if (item.isChoose) {
                    count += 1
                }
            }
        } else {
            for (item in studentList) {
                if (item.isChoose) {
                    count += 1
                }
            }
        }
        return count
    }

    // 已选老师数量 (0 为传入的列表   1 为接口列表)
    private fun haveChooseTeacherNum(type: Int): Int{
        var count = 0
        if (type == 0) {
            for (item in teacherInfoList) {
                if (item.isChoose) {
                    count += 1
                }
            }
        } else {
            for (item in teacherList) {
                if (item.isChoose) {
                    count += 1
                }
            }
        }
        return count
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
        tvClassStudentName.text = "学生(" + selectStudentCount + "/" + studentList.size + ")"
        studentAdapter.notifyDataSetChanged()
    }

    // 获取班级下老师列表
    override fun getTeacherInClasses(list: MutableList<TeacherInClassesBean>?) {
        teacherList.clear()
        // 判断是否已经有值记录了
        if (teacherInfoList.size > 0) {
            teacherList.addAll(teacherInfoList)
        } else {
            if (list != null && list.size > 0) {
                teacherList.addAll(list)
            }
        }
        tvClassTeacherName.text = "老师(" + selectTeacherCount + "/" + teacherList.size + ")"
        teacherAdapter.notifyDataSetChanged()
    }
}