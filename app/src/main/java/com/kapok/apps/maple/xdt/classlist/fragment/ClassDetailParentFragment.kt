package com.kapok.apps.maple.xdt.classlist.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.activity.AddressBookTeacherActivity
import com.kapok.apps.maple.xdt.classlist.activity.ClassInfoParentActivity
import com.kapok.apps.maple.xdt.classlist.adapter.ClassIconAdapter
import com.kapok.apps.maple.xdt.classlist.adapter.ParentWorkNoticeListAdapter
import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.bean.TeacherHomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.presenter.ClassDetailParentPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassDetailParentView
import com.kapok.apps.maple.xdt.homework.activity.CheckHomeWorkParentActivity
import com.kapok.apps.maple.xdt.homework.activity.HomeWorkParentListActivity
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListItemBean
import com.kapok.apps.maple.xdt.notice.activity.CheckNoticeParentActivity
import com.kapok.apps.maple.xdt.notice.activity.NoticeParentListActivity
import com.kapok.apps.maple.xdt.notice.bean.NoticeListItemBean
import com.kapok.apps.maple.xdt.timetable.activity.timetable_parent.TimeTableParentActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.custom.CustomLoadMoreView
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChangeUserIdentity
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.fragment_class_detail_parent.*

/**
 * 班级详情家长端(1级页面)
 * fanjie
 */
@SuppressLint("SetTextI18n")
class ClassDetailParentFragment: BaseMvpFragment<ClassDetailParentPresenter>(),ClassDetailParentView {
    // 班级Id
    private var classId: Int = -1
    private var className: String = ""
    private var grade: String = ""
    private var gradeId: Int = -1
    private var studentId: Int = -1
    // 家长端
    private val searchType = 1
    // 详情功能Bean (家委会成员需要增加 家委会Icon)
    private val classIconBean: MutableList<String> = arrayListOf("课程表", "作业", "通知", "通讯录", "账本")
    private lateinit var classIconAdapter: ClassIconAdapter
    private lateinit var params: RelativeLayout.LayoutParams
    private val Parent_INFO = 300
    // 作业通知列表集合
    private lateinit var workNoticeListParent: MutableList<TeacherHomeWorkNoticeBean>
    // 作业列表Adapter
    private lateinit var workNoticeListParentAdapter: ParentWorkNoticeListAdapter
    // 当前页
    private var pageNo: Int = 1
    // 每页记录数
    private var pageSize: Int = 10

    companion object {
        fun instance(classId: Int,studentId: Int): ClassDetailParentFragment {
            val myFragment = ClassDetailParentFragment()
            val bundle = Bundle()
            bundle.putInt("classId", classId)
            bundle.putInt("studentId",studentId)
            myFragment.arguments = bundle
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_class_detail_parent, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = ClassDetailParentPresenter(activity!!)
        mPresenter.mView = this
        // 页面初始化配置
        initData()
    }

    override fun onResume() {
        super.onResume()
        // 调用获取班级详情接口
        mPresenter.getClassDetailInfo(classId, searchType, AppPrefsUtils.getInt("userId"))
        getWorkNoticeListParent(true)
    }

    private fun initData() {
        if (arguments != null) {
            classId = arguments!!.getInt("classId")
            studentId = arguments!!.getInt("studentId")
        }
        // 配置Rv
        params = viewIndicatorParent.layoutParams as RelativeLayout.LayoutParams
        val screenWidth = resources.displayMetrics.widthPixels
        classIconAdapter = ClassIconAdapter(activity!!, classIconBean, screenWidth)
        rvClassDetailParent.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        rvClassDetailParent.adapter = classIconAdapter
        // 配置作业通知列表
        workNoticeListParent = arrayListOf()
        rvClassInfoListParent.layoutManager = LinearLayoutManager(activity)
        workNoticeListParentAdapter = ParentWorkNoticeListAdapter(activity!!, workNoticeListParent)
        workNoticeListParentAdapter.setLoadMoreView(CustomLoadMoreView())
        workNoticeListParentAdapter.setOnLoadMoreListener(
            { getWorkNoticeListParent(false) },
            rvClassInfoListParent
        )
        rvClassInfoListParent.adapter = workNoticeListParentAdapter
        rvClassInfoListParent.addItemDecoration(
            RecycleViewDivider(
                activity,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(activity!!, 12),
                resources.getColor(R.color.xdt_background)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(BaseApplication.context)
            .inflate(R.layout.layout_class_list_empty2, rvClassInfoListParent, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "当前还没有作业通知列表~"
        workNoticeListParentAdapter.emptyView = emptyView
    }

    private fun initListener() {
        // 返回
        tvClassDetailBackParent.setOnClickListener {
            // 切换班级列表页
            BaseRxBus.mBusInstance.post(EventChangeUserIdentity(BaseUserInfo.identity))
        }
        // 详情
        rlClassDetailInfoParent.setOnClickListener {
            val intent = Intent(activity, ClassInfoParentActivity::class.java)
            intent.putExtra("classId", classId)
            intent.putExtra("searchType", searchType)
            intent.putExtra("studentId", studentId)
            startActivityForResult(intent, Parent_INFO)
        }
        // 滑动监听Indicator
        rvClassDetailParent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager
                when ((manager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()) {
                    0 -> {
                        params.leftMargin = Dp2pxUtils.dp2px(activity!!, 0)
                        viewIndicatorParent.layoutParams = params
                        viewIndicatorParent.requestLayout()
                    }
                    1 -> {
                        params.leftMargin = Dp2pxUtils.dp2px(activity!!, 10)
                        viewIndicatorParent.layoutParams = params
                        viewIndicatorParent.requestLayout()
                    }
                    2 -> {
                        params.leftMargin = Dp2pxUtils.dp2px(activity!!, 20)
                        viewIndicatorParent.layoutParams = params
                        viewIndicatorParent.requestLayout()
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
                        val intent = Intent(
                            activity!!,
                            TimeTableParentActivity::class.java
                        )
                        intent.putExtra("classId", classId)
                        startActivity(intent)
                    }
                    1 -> {
                        // 作业
                        val intent = Intent(activity!!, HomeWorkParentListActivity::class.java)
                        intent.putExtra("classId", classId)
                        startActivity(intent)
                    }
                    2 -> {
                        // 通知
                        val intent = Intent(activity!!, NoticeParentListActivity::class.java)
                        intent.putExtra("classId",classId)
                        startActivity(intent)
                    }
                    3 -> {
                        // 通讯录 (家长和普通老师进让的是同一个界面)
                        val intent = Intent(activity!!, AddressBookTeacherActivity::class.java)
                        intent.putExtra("classId",classId)
                        intent.putExtra("isHeaderTeacher",false)
                        startActivity(intent)
                    }
                }
            }
        // 通知作业列表点击事件
        workNoticeListParentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                // 1 作业 2 通知
                val item = workNoticeListParent[position]
                if (item.type == 1) {
                    val intent = Intent(
                        activity!!,
                        CheckHomeWorkParentActivity::class.java
                    )
                    intent.putExtra("homeWorkItemInfo", HomeWorkListItemBean(
                        item.content,item.deadline,item.gmtCreate,item.images,false,item.remainTime,item.subjectId,item.subjectName,item.teacherAvatar,item.teacherId,
                        item.teacherName,item.title,item.workId,item.state,item.submitStatus,item.replyStatus,item.studentId
                    )
                    )
                    intent.putExtra("classId",classId)
                    startActivity(intent)
                } else {
                    val intent = Intent(
                        activity!!,
                        CheckNoticeParentActivity::class.java
                    )
                    intent.putExtra("noticeItemInfo", NoticeListItemBean(item.classId,item.className,item.content,item.deadline,item.gmtCreate,item.grade,item.gradeId,item.images,
                        false,item.readStatus,item.receiptStatus,item.remainTime,item.replyStatus,item.schoolId,item.schoolName,item.startYear,item.subjectId,item.subjectName,
                        item.submitStatus,item.teacherAvatar,item.teacherId,item.teacherName,item.title,item.workId,item.state,item.studentId,item.workType,item.workTypeDesc)
                    )
                    intent.putExtra("classId",classId)
                    startActivity(intent)
                }
            }
    }

    private fun getWorkNoticeListParent(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getParentWorkNoticeList(
            classId,
            1,
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
        tvClassDetailTitleParent.text = bean.grade + bean.className + "班"
        className = bean.className
        grade = bean.grade
        gradeId = bean.gradeId
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(activity!!, bean.avatar, ivClassDetailParent)
        } else {
            Glide.with(this).load(R.mipmap.def_head_class).into(ivClassDetailParent)
        }
        // 编号
        tvClassDetailNumberParent.text = "班级编号：" + bean.classId.toString()
        // 班主任
        tvClassDetailParentName.text = "班主任：" + bean.headerTeacher
        // 学校
        tvClassDetailSchoolNameParent.text = "学校：" + bean.schoolName
        // 老师数量 学生数量 家长数量
        tvClassDetailNumParent.text = bean.teacherCount.toString()
        tvClassDetailStudentNumParent.text = bean.studentCount.toString()
        tvClassDetailParentNumParent.text = bean.parentCount.toString()
    }

    override fun getParentNoticeWorkBean(bean: HomeWorkNoticeBean, isFirst: Boolean) {
        // 列表
        if (isFirst) {
            workNoticeListParent.clear()
            workNoticeListParentAdapter.setNewData(bean.list)
            rvClassInfoListParent.scrollToPosition(0)
            if (bean.list != null && bean.list.isNotEmpty()) {
                for (item in bean.list) {
                    workNoticeListParent.add(item)
                }
            }
        } else {
            if (bean.list != null && bean.list.isNotEmpty()) {
                workNoticeListParentAdapter.addData(bean.list)
                for (item in bean.list) {
                    workNoticeListParent.add(item)
                }
            }
        }
        workNoticeListParentAdapter.setEnableLoadMore(workNoticeListParentAdapter.itemCount - 1 < bean.total)
        if (workNoticeListParentAdapter.itemCount - 1 < bean.total) {
            workNoticeListParentAdapter.loadMoreComplete()
        } else {
            workNoticeListParentAdapter.loadMoreEnd()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Parent_INFO -> {
                    // 退出班级后 replace 到班级列表页
                    BaseRxBus.mBusInstance.post(EventChangeUserIdentity(BaseUserInfo.identity))
                }
            }
        }
    }
}