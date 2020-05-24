package com.kapok.apps.maple.xdt.classlist.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.activity.AddressBookTeacherActivity
import com.kapok.apps.maple.xdt.classlist.adapter.ClassIconAdapter
import com.kapok.apps.maple.xdt.classlist.adapter.TeacherWorkNoticeListAdapter
import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.bean.TeacherHomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.custom.CustomBottomClassListDialog
import com.kapok.apps.maple.xdt.classlist.presenter.ClassDetailTeacherPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassDetailTeacherView
import com.kapok.apps.maple.xdt.homework.activity.CheckHomeWorkTeacherActivity
import com.kapok.apps.maple.xdt.homework.activity.HomeWorkTeacherListActivity
import com.kapok.apps.maple.xdt.homework.activity.SendHomeWorkActivity
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListItemBean
import com.kapok.apps.maple.xdt.notice.activity.CheckNoticeTeacherActivity
import com.kapok.apps.maple.xdt.notice.activity.NoticeTeacherListActivity
import com.kapok.apps.maple.xdt.notice.activity.SendNoticeActivity
import com.kapok.apps.maple.xdt.notice.bean.NoticeListItemBean
import com.kapok.apps.maple.xdt.timetable.activity.timetable_teacher.TimeTableTeacherActivity
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.custom.CustomLoadMoreView
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_class_detail_teacher.*

/**
 * 老师班级详情页
 */
@SuppressLint("SetTextI18n")
class ClassDetailTeacherActivity : BaseMVPActivity<ClassDetailTeacherPresenter>(), ClassDetailTeacherView {
    // 班级Id
    private var classId: Int = -1
    private var className: String = ""
    private var grade: String = ""
    private var gradeId: Int = -1
    // 教师端
    private val searchType = 2
    // 设置弹窗
    private lateinit var classDetailSettingDialog: CustomCancelBottomDialog
    // 选择老师弹窗
    private lateinit var bottomTeacherDialog: CustomBottomClassListDialog
    private lateinit var teacherList: MutableList<TeacherOutPutVOList>
    private lateinit var confirmDialog: CancelConfirmDialog
    // 详情功能Bean
    private val classIconBean: MutableList<String> = arrayListOf("课程表", "作业", "通知", "通讯录", "账本", "邀请", "家委会")
    private lateinit var classIconAdapter: ClassIconAdapter
    private lateinit var params: RelativeLayout.LayoutParams
    // 当前班主任Id
    private var currentHeaderTeacherId: Int = -1
    // 判断是否是班主任
    private var isHeaderTeacher: Boolean = false
    // RequestCode
    private val Teacher_INFO = 100
    // 作业通知列表集合
    private lateinit var workNoticeListTeacher: MutableList<TeacherHomeWorkNoticeBean>
    // 作业列表Adapter
    private lateinit var workNoticeListTeacherAdapter: TeacherWorkNoticeListAdapter
    // 当前页
    private var pageNo: Int = 1
    // 每页记录数
    private var pageSize: Int = 10
    // 底部弹窗Pop
    private lateinit var pop: PopupWindow
    private lateinit var parentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail_teacher)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = ClassDetailTeacherPresenter(this)
        mPresenter.mView = this
        // 页面初始化配置
        initData()
    }

    override fun onResume() {
        super.onResume()
        // 调用获取班级详情接口
        mPresenter.getClassDetailInfo(classId, searchType, AppPrefsUtils.getInt("userId"))
        getWorkNoticeListTeacher(true)
    }

    private fun initData() {
        val intent = intent
        classId = intent.getIntExtra("classId", -1)
        // 配置Rv
        params = viewIndicator.layoutParams as RelativeLayout.LayoutParams
        val screenWidth = resources.displayMetrics.widthPixels
        classIconAdapter = ClassIconAdapter(this, classIconBean, screenWidth)
        rvClassDetailTeacher.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvClassDetailTeacher.adapter = classIconAdapter
        // 调用获取老师列表接口
        teacherList = arrayListOf()
        mPresenter.getClassTeacherList(classId)
        // 配置列表Rv
        workNoticeListTeacher = arrayListOf()
        rvClassInfoListTeacher.layoutManager = LinearLayoutManager(this)
        workNoticeListTeacherAdapter = TeacherWorkNoticeListAdapter(this, workNoticeListTeacher)
        workNoticeListTeacherAdapter.setLoadMoreView(CustomLoadMoreView())
        workNoticeListTeacherAdapter.setOnLoadMoreListener(
            { getWorkNoticeListTeacher(false) },
            rvClassInfoListTeacher
        )
        rvClassInfoListTeacher.adapter = workNoticeListTeacherAdapter
        rvClassInfoListTeacher.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 12),
                resources.getColor(R.color.xdt_background)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty2, rvClassInfoListTeacher, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "当前还没有作业通知列表~"
        workNoticeListTeacherAdapter.emptyView = emptyView
        // Pop
        initPop()
    }

    private fun initPop() {
        pop = PopupWindow(this)
        parentView = LayoutInflater.from(this).inflate(R.layout.layout_class_list, null)
        pop.contentView = parentView
        pop.height = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.width = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.isTouchable = true
        pop.isFocusable = true
        pop.setBackgroundDrawable(resources.getDrawable(R.color.transparent))
        pop.isOutsideTouchable = true
        pop.update()
        pop.setOnDismissListener {
            val lp = window.attributes
            lp.alpha = 1f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = lp
        }
    }

    private fun initListener() {
        // 返回
        ivClassDetailBackTeacher.setOnClickListener { finish() }
        // 设置
        ivClassDetailSetting.setOnClickListener {
            // dialog
            classDetailSettingDialog = CustomCancelBottomDialog(this@ClassDetailTeacherActivity, R.style.BottomDialog)
            classDetailSettingDialog.addItem(
                "转移班主任权限",
                R.color.text_xdt,
                View.OnClickListener {
                    // 判断该班级是否有老师  选择老师弹窗(每次进入清空选中状态)
                    if (teacherList.size > 0) {
                        for (item in teacherList) {
                            item.isSelected = false
                        }
                        bottomTeacherDialog =
                            CustomBottomClassListDialog(this@ClassDetailTeacherActivity, R.style.BottomDialog)
                        bottomTeacherDialog.setTitle("选择教师")
                        bottomTeacherDialog.addItem(teacherList)
                        bottomTeacherDialog.show()
                        bottomTeacherDialog.setOnselectIndexListener(object :
                            CustomBottomClassListDialog.SelectIndexListener {
                            override fun selectIndex(position: Int) {
                                confirmDialog = CancelConfirmDialog(
                                    this@ClassDetailTeacherActivity, R.style.BottomDialog
                                    , "请确认是否将班主任权限交给", teacherList[position].teacherName
                                )
                                confirmDialog.show()
                                confirmDialog.setOnClickConfirmListener(object :
                                    CancelConfirmDialog.ClickConfirmListener {
                                    override fun confirm() {
                                        // 调用转移班主任权限接口
                                        mPresenter.changeHeaderTeacher(
                                            classId,
                                            teacherList[position].teacherId,
                                            currentHeaderTeacherId
                                        )
                                        confirmDialog.dismiss()
                                    }
                                })
                            }
                        })
                        classDetailSettingDialog.dismiss()
                    } else {
                        ToastUtils.showMsg(this@ClassDetailTeacherActivity, "您的班级没有其他老师\n无法转移权限")
                    }
                })
            classDetailSettingDialog.addItem(
                "班级升学",
                R.color.text_xdt,
                View.OnClickListener {
                    mPresenter.classUpdate(classId, className, grade, gradeId, AppPrefsUtils.getInt("userId"))
                    classDetailSettingDialog.dismiss()
                })
            classDetailSettingDialog.addItem(
                "解散班级",
                R.color.xdt_exit_text,
                View.OnClickListener {
                    confirmDialog = CancelConfirmDialog(
                        this@ClassDetailTeacherActivity, R.style.BottomDialog
                        , "请确认是否解散当前班级", ""
                    )
                    confirmDialog.show()
                    confirmDialog.setOnClickConfirmListener(object :
                        CancelConfirmDialog.ClickConfirmListener {
                        override fun confirm() {
                            // 调用解散班级接口
                            mPresenter.dissolvedClass(classId, searchType, AppPrefsUtils.getInt("userId"))
                            confirmDialog.dismiss()
                        }
                    })
                    classDetailSettingDialog.dismiss()
                })
            classDetailSettingDialog.show()
        }
        // 详情
        rlClassDetailInfoTeacher.setOnClickListener {
            val intent = Intent(this, ClassInfoTeacherActivity::class.java)
            intent.putExtra("classId", classId)
            intent.putExtra("searchType", searchType)
            startActivityForResult(intent, Teacher_INFO)
        }
        // 滑动监听Indicator
        rvClassDetailTeacher.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager
                when ((manager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()) {
                    0 -> {
                        params.leftMargin = Dp2pxUtils.dp2px(this@ClassDetailTeacherActivity, 0)
                        viewIndicator.layoutParams = params
                        viewIndicator.requestLayout()
                    }
                    1 -> {
                        params.leftMargin = Dp2pxUtils.dp2px(this@ClassDetailTeacherActivity, 10)
                        viewIndicator.layoutParams = params
                        viewIndicator.requestLayout()
                    }
                    2 -> {
                        params.leftMargin = Dp2pxUtils.dp2px(this@ClassDetailTeacherActivity, 20)
                        viewIndicator.layoutParams = params
                        viewIndicator.requestLayout()
                    }
                }
            }
        })
        // Icon
        classIconAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                when (position) {
                    0 -> {
                        // 课程表
                        val intent = Intent(this@ClassDetailTeacherActivity, TimeTableTeacherActivity::class.java)
                        intent.putExtra("classId",classId)
                        startActivity(intent)
                    }
                    1 -> {
                        // 作业
                        val intent = Intent(this@ClassDetailTeacherActivity, HomeWorkTeacherListActivity::class.java)
                        intent.putExtra("classId",classId)
                        intent.putExtra("isHeaderTeacher",isHeaderTeacher)
                        intent.putExtra("from",false)
                        startActivity(intent)
                    }
                    2 -> {
                        // 通知
                        val intent = Intent(this,NoticeTeacherListActivity::class.java)
                        intent.putExtra("classId",classId)
                        intent.putExtra("isHeaderTeacher",isHeaderTeacher)
                        intent.putExtra("from",false)
                        startActivity(intent)
                    }
                    3 -> {
                        // 通讯录
                        val intent = Intent(this,AddressBookTeacherActivity::class.java)
                        intent.putExtra("classId",classId)
                        intent.putExtra("isHeaderTeacher",isHeaderTeacher)
                        startActivity(intent)
                    }
                }
            }
        // 通知作业列表点击事件
        workNoticeListTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                // 1 作业 2 通知
                val item = workNoticeListTeacher[position]
                if (item.type == 1) {
                    val intent = Intent(
                        this@ClassDetailTeacherActivity,
                        CheckHomeWorkTeacherActivity::class.java
                    )
                    intent.putExtra("homeWorkItemInfo", HomeWorkListItemBean(
                        item.content,item.deadline,item.gmtCreate,item.images,false,item.remainTime,item.subjectId,item.subjectName,item.teacherAvatar,item.teacherId,
                        item.teacherName,item.title,item.workId,item.state,item.submitStatus,item.replyStatus,item.studentId
                    ))
                    startActivity(intent)
                } else {
                    val intent = Intent(
                        this@ClassDetailTeacherActivity,
                        CheckNoticeTeacherActivity::class.java
                    )
                    intent.putExtra("noticeItemInfo", NoticeListItemBean(item.classId,item.className,item.content,item.deadline,item.gmtCreate,item.grade,item.gradeId,item.images,
                        false,item.readStatus,item.receiptStatus,item.remainTime,item.replyStatus,item.schoolId,item.schoolName,item.startYear,item.subjectId,item.subjectName,
                        item.submitStatus,item.teacherAvatar,item.teacherId,item.teacherName,item.title,item.workId,item.state,item.studentId,item.workType,item.workTypeDesc))
                    startActivity(intent)
                }
            }
        // 底部Pop
        FABClassListTeacher.setOnClickListener { v -> showUp2(v) }
        parentView.findViewById<TextView>(R.id.tvClassNotice)
            .setOnClickListener {
                // 发通知
                val intent = Intent(this, SendNoticeActivity::class.java)
                startActivity(intent)
                pop.dismiss()
            }
        parentView.findViewById<TextView>(R.id.tvClassHomeWork)
            .setOnClickListener {
                // 发作业
                val intent = Intent(this, SendHomeWorkActivity::class.java)
                intent.putExtra("fromCheckHomeWork", false)
                startActivity(intent)
                pop.dismiss()
            }
        parentView.findViewById<TextView>(R.id.tvClassMoney)
            .setOnClickListener {
                ToastUtils.showMsg(this, "记账")
                pop.dismiss()
            }
    }

    private fun showUp2(v: View) {
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = parentView.measuredHeight
        val popupWidth = parentView.measuredWidth

        // 产生背景变暗效果
        val lp =  window.attributes
        lp.alpha = 0.6f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
        //获取需要在其上方显示的控件的位置信息
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        //在控件上方显示
        pop.showAtLocation(
            v,
            Gravity.NO_GRAVITY,
            (location[0] + v.width / 2) - popupWidth / 2 - 100,
            location[1] - popupHeight - 50
        )
    }

    // 获取作业通知回调
    private fun getWorkNoticeListTeacher(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getTeacherWorkNoticeList(
            classId,
            2,
            false,
            pageNo,
            pageSize,
            "",
            "",
            0,
            0,
            0,
            0,
            AppPrefsUtils.getInt("userId"),
            isFirst
        )
    }

    // 班级详情回调
    override fun getClassDetailInfo(bean: ClassDetailInfoBean) {
        // 班级名称
        tvClassDetailTitleTeacher.text = bean.grade + bean.className + "班"
        className = bean.className
        grade = bean.grade
        gradeId = bean.gradeId
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, ivClassDetailTeacher)
        } else {
            Glide.with(this).load(R.mipmap.def_head_class).into(ivClassDetailTeacher)
        }
        // 编号
        tvClassDetailNumberTeacher.text = "班级编号：" + bean.classId.toString()
        // 班主任
        tvClassDetailTeacherName.text = "班主任：" + bean.headerTeacher
        // 学校
        tvClassDetailSchoolName.text = "学校：" + bean.schoolName
        // 老师数量 学生数量 家长数量
        tvClassDetailTeacherNum.text = bean.teacherCount.toString()
        tvClassDetailStudentNum.text = bean.studentCount.toString()
        tvClassDetailParentNum.text = bean.parentCount.toString()
        // 当前班主任Id
        currentHeaderTeacherId = bean.headerTeacherId
        // 是否是班主任
        isHeaderTeacher = AppPrefsUtils.getInt("userId") == bean.headerTeacherId
        if (isHeaderTeacher) {
            ivClassDetailSetting.setVisible(true)
        } else {
            ivClassDetailSetting.setVisible(false)
        }
    }

    // 班级老师列表
    override fun getClassTeacherList(list: MutableList<TeacherOutPutVOList>?) {
        if (list != null && list.size > 0) {
            teacherList.addAll(list)
        }
    }

    // 转移班主任权限回调
    override fun changeHeaderTeacher(msg: String) {
        ToastUtils.showMsg(this, msg)
        mPresenter.getClassDetailInfo(classId, searchType, AppPrefsUtils.getInt("userId"))
    }

    // 解散班级接口回调
    override fun dissolvedClass(msg: String) {
        ToastUtils.showMsg(this, msg)
        finish()
    }

    // 班级升学回调
    override fun classUpdate(msg: String) {
        ToastUtils.showMsg(this, msg)
        mPresenter.getClassDetailInfo(classId, searchType, AppPrefsUtils.getInt("userId"))
    }

    // 获取老师作业通知列表接口
    override fun getTeacherNoticeWorkBean(bean: HomeWorkNoticeBean,isFirst: Boolean) {
        // 列表
        if (isFirst) {
            workNoticeListTeacher.clear()
            workNoticeListTeacherAdapter.setNewData(bean.list)
            rvClassInfoListTeacher.scrollToPosition(0)
            if (bean.list != null && bean.list.isNotEmpty()) {
                for (item in bean.list) {
                    workNoticeListTeacher.add(item)
                }
            }
        } else {
            if (bean.list != null && bean.list.isNotEmpty()) {
                workNoticeListTeacherAdapter.addData(bean.list)
                for (item in bean.list) {
                    workNoticeListTeacher.add(item)
                }
            }
        }
        workNoticeListTeacherAdapter.setEnableLoadMore(workNoticeListTeacherAdapter.itemCount - 1 < bean.total)
        if (workNoticeListTeacherAdapter.itemCount - 1 < bean.total) {
            workNoticeListTeacherAdapter.loadMoreComplete()
        } else {
            workNoticeListTeacherAdapter.loadMoreEnd()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Teacher_INFO -> {
                    finish()
                }
            }
        }
    }
}