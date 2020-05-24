package com.kapok.apps.maple.xdt.timetable.activity

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.adapter.BottomTeacherListDialogAdapter
import com.kapok.apps.maple.xdt.timetable.adapter.TimeTableChooseSubjectAdapter
import com.kapok.apps.maple.xdt.timetable.bean.SubjectTeacherListBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kapok.apps.maple.xdt.timetable.presenter.TimeTableChooseSubjectPresenter
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableChooseSubjectView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_timetable_choose_subject.*

/**
 * 课程表 选择课程页面
 * fanjie
 */
class TimeTableChooseSubjectActivity : BaseMVPActivity<TimeTableChooseSubjectPresenter>(), TimeTableChooseSubjectView {
    // 用户Id
    private var userId = -1
    // 课程列表
    private lateinit var subjectList: ArrayList<ClassChooseSubjectBean>
    // 老师列表
    private lateinit var teacherList: ArrayList<TeacherOutPutVOList>
    private lateinit var tempTeacherList: ArrayList<TeacherOutPutVOList>
    // 创建新科目弹窗
    private lateinit var newSubjectDialog: Dialog
    // 老师底部弹窗
    private lateinit var teacherBottomDialog: Dialog
    private lateinit var bottomTeacherAdapter: BottomTeacherListDialogAdapter
    // Adapter
    private lateinit var subjectAdapter: TimeTableChooseSubjectAdapter
    // 保存 已选中的 课程 和 老师集合
    private lateinit var subjectTeacherListBean: MutableList<SubjectTeacherListBean>
    // 当前选中的课程项id / 老师id
    private var currentSelectSubjectId: Int = -1
    // 自定义课程名称
    private var customSubjectName = ""

    private var classId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable_choose_subject)
        initView()
        initData()
        initDialog()
        initListener()
    }

    private fun initView() {
        mPresenter = TimeTableChooseSubjectPresenter(this@TimeTableChooseSubjectActivity)
        mPresenter.mView = this
        classId = intent.getIntExtra("classId",-1)
        userId = AppPrefsUtils.getInt("userId")
        // 保存
        headerBar_TimetableChooseSubject.getRightView()
            .setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        headerBar_TimetableChooseSubject.getRightView().text = "保存"
        headerBar_TimetableChooseSubject.getRightView().setVisible(true)
        // 配置Rv
        subjectList = arrayListOf()
        subjectTeacherListBean = arrayListOf()
        subjectAdapter = TimeTableChooseSubjectAdapter(this, subjectList)
        rvSubject.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSubject.adapter = subjectAdapter
        rvSubject.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
    }

    private fun initData() {
        // 调用获取课程列表接口
        mPresenter.getClassSubjectList(classId)
        // 调用根据班级获取老师列表接口
        mPresenter.getClassTeacherList(classId)
    }

    private fun initListener() {
        // 保存点击事件
        headerBar_TimetableChooseSubject.getRightView().setOnClickListener {
            subjectTeacherListBean.clear()
            for (item in subjectList) {
                if (item.isSelected) {
                    if (item.teacherids.isNullOrEmpty()) {
                        subjectTeacherListBean.add(SubjectTeacherListBean(item.subjectId, ""))
                    } else {
                        subjectTeacherListBean.add(SubjectTeacherListBean(item.subjectId, item.teacherids))
                    }
                }
            }
            mPresenter.saveClassSubject(classId, subjectTeacherListBean, userId)
        }
        // 添加新科目点击事件
        tvAddNewSubject.setOnClickListener { newSubjectDialog.show() }
        // 选中科目点击事件 / 选择老师点击事件
        subjectAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvChooseSubject -> {
                        subjectList[position].isSelected = !subjectList[position].isSelected
                        subjectAdapter.notifyDataSetChanged()
                    }
                    R.id.ivCheckSelected -> {
                        subjectList[position].isSelected = !subjectList[position].isSelected
                        subjectAdapter.notifyDataSetChanged()
                    }
                    R.id.tvChooseTeacher -> {
                        // 当前课程的teacherlist
                        for (item in teacherList) {
                            item.isSelected = false
                        }
                        val selectTeacherList = subjectList[position].teacherOutPutVOList
                        currentSelectSubjectId = subjectList[position].subjectId
                        if (selectTeacherList.size > 0) {
                            for (item in selectTeacherList) {
                                for (itemTeacherList in teacherList) {
                                    if (item.teacherId == itemTeacherList.teacherId) {
                                        itemTeacherList.isSelected = true
                                    }
                                }
                            }
                        }
                        bottomTeacherAdapter.setNewData(teacherList)
                        teacherBottomDialog.show()
                    }
                }
            }
        // 选择老师底部弹窗点击事件
        bottomTeacherAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.rlTeaherListItem -> {
                        tempTeacherList[position].isSelected = !tempTeacherList[position].isSelected
                        bottomTeacherAdapter.setNewData(tempTeacherList)
                    }
                }
            }
    }

    private fun initDialog() {
        // 创建新科目弹窗
        newSubjectDialog = Dialog(this, R.style.BottomDialog)
        val subjectView = LayoutInflater.from(this).inflate(R.layout.dialog_new_subject, null)
        newSubjectDialog.setContentView(subjectView)
        initWindow(newSubjectDialog, false)
        val etNewSubject = subjectView.findViewById<EditText>(R.id.etNewSubject)
        val tvNewSubjectCancel = subjectView.findViewById<TextView>(R.id.tvNewSubjectCancel)
        val tvNewSubjectConfirm = subjectView.findViewById<TextView>(R.id.tvNewSubjectConfirm)
        tvNewSubjectCancel.setOnClickListener { newSubjectDialog.dismiss() }
        tvNewSubjectConfirm.setOnClickListener {
            customSubjectName = etNewSubject.text.toString().trim()
            // 调用创建新课程接口
            mPresenter.createNewSubject(classId, customSubjectName)
        }
        // 老师 底部弹窗
        teacherBottomDialog = Dialog(this, R.style.BottomDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_teacher_list, null)
        teacherBottomDialog.setContentView(view)
        initWindow(teacherBottomDialog, true)
        // 配置Rv
        teacherList = arrayListOf()
        tempTeacherList = arrayListOf()
        bottomTeacherAdapter = BottomTeacherListDialogAdapter(this, teacherList)
        val rvTeacher = view.findViewById<RecyclerView>(R.id.rvTeacherList)
        rvTeacher.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTeacher.adapter = bottomTeacherAdapter
        rvTeacher.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
        val tvTeacherCancel = view.findViewById<TextView>(R.id.tv_bottomdialog_cancel_teacherlist)
        val tvTeacherConfirm = view.findViewById<TextView>(R.id.tv_bottomdialog_confirm_teacherlist)
        tvTeacherCancel.setOnClickListener { teacherBottomDialog.dismiss() }
        tvTeacherConfirm.setOnClickListener {
            val dataList = arrayListOf<TeacherOutPutVOList>()
            dataList.clear()
            var teacherids = ""
            teacherList.clear()
            teacherList.addAll(tempTeacherList)
            for (item in teacherList) {
                if (item.isSelected) {
                    dataList.add(item)
                }
            }
            if (dataList.size > 0) {
                for (item in dataList) {
                    teacherids = teacherids + item.teacherId + ","
                }
            }
            for (item in subjectList) {
                if (item.subjectId == currentSelectSubjectId) {
                    item.teacherOutPutVOList = dataList
                    if (dataList.size > 0) {
                        item.teacherids = teacherids.substring(0, teacherids.length - 1)
                    } else {
                        item.teacherids = teacherids
                    }
                }
            }
            teacherBottomDialog.dismiss()
            subjectAdapter.notifyDataSetChanged()
        }
    }

    private fun initWindow(dialog: Dialog, isBottom: Boolean) {
        val window = dialog.window
        val lp = window!!.attributes
        val d = window.windowManager.defaultDisplay
        lp.dimAmount = 0.3f
        lp.width = d.width
        window.attributes = lp
        if (isBottom) {
            window.setGravity(Gravity.BOTTOM)
        } else {
            window.setGravity(Gravity.CENTER)
        }
        // 设置点击外围消散
        dialog.setCanceledOnTouchOutside(true)
    }

    // 列表接口回调
    override fun getClassSubjectList(dataList: MutableList<ClassChooseSubjectBean>?) {
        if (dataList != null && dataList.size > 0) {
            subjectList.clear()
            subjectList.addAll(dataList)
            subjectAdapter.notifyDataSetChanged()
        }
    }

    // 获取老师列表回调
    override fun getClassTeacherList(dataList: MutableList<TeacherOutPutVOList>?) {
        if (dataList != null && dataList.size > 0) {
            teacherList.clear()
            tempTeacherList.clear()
            teacherList.addAll(dataList)
            tempTeacherList.addAll(dataList)
            bottomTeacherAdapter.notifyDataSetChanged()
        }
    }

    // 保存课程老师的回调
    override fun saveClassSubject(msg: String) {
        ToastUtils.showMsg(this, msg)
        finish()
    }

    // 创建新课程的回调
    override fun createNewClass(subjectId: Int) {
        ToastUtils.showMsg(this, "保存成功")
        newSubjectDialog.dismiss()
        subjectList.add(
            ClassChooseSubjectBean(
                isChoose = false,
                isSelected = true,
                ownerType = 2,
                subjectId = subjectId,
                subjectName = customSubjectName,
                teacherids = "",
                teacherOutPutVOList = arrayListOf()
            )
        )
        subjectAdapter.notifyDataSetChanged()
    }
}