package com.kapok.apps.maple.xdt.homework.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.CheckHomeWorkTeacherAdapter
import com.kapok.apps.maple.xdt.homework.adapter.TeacherCommentAdapter
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkAnswerBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherCommentParentBean
import com.kapok.apps.maple.xdt.homework.presenter.StudentAnswerPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.StudentAnswerView
import com.kapok.apps.maple.xdt.utils.PhotoShowActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_student_answer.*
import java.io.Serializable
import android.view.WindowManager
import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog2
import com.kotlin.baselibrary.utils.ToastUtils

@SuppressLint("SetTextI18n")
class StudentAnswerActivity : BaseMVPActivity<StudentAnswerPresenter>(), StudentAnswerView {
    // workId studentId
    private var workId = 0
    private var studentId = 0
    private var classId = 0
    private var parentId = 0
    private var workAnswerId = 0
    private var studentName = ""
    private var studentPic = ""
    // 展示图片的列表
    private lateinit var homeWorkPicList: MutableList<String>
    private lateinit var homeWorkAnswerAdapter: CheckHomeWorkTeacherAdapter
    // 展示评论的列表
    private lateinit var teacherCommentAdapter: TeacherCommentAdapter
    private lateinit var dataList: MutableList<TeacherCommentParentBean>
    // 常用提示语弹窗
    private lateinit var checkHomeWorkBottomDialog: CustomCancelBottomDialog2
    private lateinit var commonCommentList: MutableList<CommonComment>
    // 跳转评论RequestCode
    private val COMMENT_SETTING = 106

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_student_answer)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = StudentAnswerPresenter(this)
        mPresenter.mView = this
        // 获取传参
        workId = intent.getIntExtra("workId", 0)
        studentId = intent.getIntExtra("studentId", 0)
        classId = intent.getIntExtra("classId", 0)
        studentName = intent.getStringExtra("studentName")
        studentPic = intent.getStringExtra("studentPic")
        // 配置图片Rv
        homeWorkPicList = arrayListOf()
        rvStudentAnswerPic.layoutManager = GridLayoutManager(this, 3)
        homeWorkAnswerAdapter = CheckHomeWorkTeacherAdapter(this, homeWorkPicList)
        rvStudentAnswerPic.adapter = homeWorkAnswerAdapter
        rvStudentAnswerPic.isNestedScrollingEnabled = false
        // 配置评论Rv
        dataList = arrayListOf()
        teacherCommentAdapter = TeacherCommentAdapter(this, dataList)
        rvCheckHomeWorkTeacherComment.adapter = teacherCommentAdapter
        rvCheckHomeWorkTeacherComment.layoutManager = LinearLayoutManager(this)
        rvCheckHomeWorkTeacherComment.isNestedScrollingEnabled = false
        // emptyView
        val emptyView = LayoutInflater.from(this)
            .inflate(R.layout.layout_class_list_empty3, rvCheckHomeWorkTeacherComment, false)
        teacherCommentAdapter.emptyView = emptyView
        // 获取常用评语设置
        commonCommentList = arrayListOf()
        // 调用作业接口
        mPresenter.getWorkAnswer(classId, studentId, workId)
        // 调用获取教师评论接口
        mPresenter.getHomeWorkComment(studentId, AppPrefsUtils.getInt("userId"), workId)
    }

    override fun onResume() {
        super.onResume()
        // 获取常用评语接口
        mPresenter.getCommonComment(AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 返回
        ivStudentAnswerBack.setOnClickListener { finish() }
        // 图片点击查看大图
        homeWorkAnswerAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent =
                    Intent(this, PhotoShowActivity::class.java)
                intent.putExtra("showUrlList", homeWorkPicList as Serializable)
                intent.putExtra("isUrlList", true)
                intent.putExtra("index", position)
                startActivity(intent)
            }
        // 设置常用语弹窗
        tvCreateComment.setOnClickListener {
            // dialog
            checkHomeWorkBottomDialog = CustomCancelBottomDialog2(this, R.style.BottomDialog)
            checkHomeWorkBottomDialog.addTitle("常用评语", R.color.text_xdt)
            checkHomeWorkBottomDialog.setBottomConfirm("设置常用语")
            for (item in commonCommentList) {
                checkHomeWorkBottomDialog.addItem(
                    item.content,
                    R.color.text_xdt,
                    View.OnClickListener {
                        etComment.text = SpannableStringBuilder(item.content)
                        checkHomeWorkBottomDialog.dismiss()
                    }
                )
            }
            // 跳转自定义常用语
            checkHomeWorkBottomDialog.setOnConfirmButtonListener(object :
                CustomCancelBottomDialog2.OnConfirmButtonClickListener {
                override fun onConfirmButton() {
                    checkHomeWorkBottomDialog.dismiss()
                    val intent = Intent(this@StudentAnswerActivity, SettingCommentActivity::class.java)
                    startActivityForResult(intent, COMMENT_SETTING)
                }
            })
            checkHomeWorkBottomDialog.show()
        }
        // 发布
        tvSendComment.setOnClickListener {
            mPresenter.createTeacherWorkComment(
                etComment.text.toString(),
                parentId,
                studentId,
                AppPrefsUtils.getInt("userId"),
                workAnswerId,
                workId
            )
        }
    }

    // 获取作业作答回调
    override fun getHomeWorkAnswer(bean: HomeWorkAnswerBean) {
        // 头像
        if (studentPic.isNotEmpty()) {
            GlideUtils.loadImage(this, studentPic, civStudentAnswer)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civStudentAnswer)
        }
        // 姓名
        tvStudentAnswerName.text =
            studentName + "(" + bean.startYear + "级" + bean.grade + bean.className + ")"
        // 时间
        tvStudentAnswerTime.text = bean.createTime
        // 内容
        if (bean.content.isNotEmpty()) {
            tvStudentAnswerContent.text = bean.content
        }
        // 图片
        if (bean.images.isNotEmpty()) {
            val picList = bean.images.split(",")
            homeWorkPicList.clear()
            for (item in picList) {
                homeWorkPicList.add(item)
            }
        }
        // 各种Id
        parentId = bean.patriarchId
        workAnswerId = bean.workAnswerId
        homeWorkAnswerAdapter.notifyDataSetChanged()
    }

    // 获取作业评论回调
    override fun getHomeWorkComment(list: MutableList<TeacherCommentParentBean>?) {
        if (list != null && list.size > 0) {
            dataList.clear()
            dataList.addAll(list)
        }
        teacherCommentAdapter.notifyDataSetChanged()
    }

    // 获取常用平用语回调
    override fun getCommonComment(list: MutableList<CommonComment>?) {
        if (list != null && list.size > 0) {
            commonCommentList.clear()
            commonCommentList.addAll(list)
        }
    }

    // 提交教师评语回调
    override fun createTeacherWorkComment(msg: String) {
        ToastUtils.showMsg(this, msg)
        etComment.text = SpannableStringBuilder("")
        // 调用获取教师评论接口
        mPresenter.getHomeWorkComment(studentId, AppPrefsUtils.getInt("userId"), workId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                COMMENT_SETTING -> {
                    val comment = data?.getStringExtra("comment")
                    etComment.text = SpannableStringBuilder(comment)
                }
            }
        }
    }
}