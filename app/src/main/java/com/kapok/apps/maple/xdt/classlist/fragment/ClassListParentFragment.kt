package com.kapok.apps.maple.xdt.classlist.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.adapter.ClassListParentAdapter
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.classlist.presenter.ClassListParentPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassListParentView
import com.kapok.apps.maple.xdt.home.activity.AddChildActivity
import com.kapok.apps.maple.xdt.home.commen.ChildrenInfoBean
import com.kapok.apps.maple.xdt.notice.activity.NoticeParentListActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_student.JoinClassActivity
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.rx.event.EventClassListBean
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.CallPhoneUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_class_list_parent.*

/**
 * 班级列表家长端
 * fanjie
 */
class ClassListParentFragment : BaseMvpFragment<ClassListParentPresenter>(), ClassListParentView {
    private var userId: Int = -1
    // 班级列表集合
    private lateinit var classListBean: MutableList<ParentClassListBean>
    // 班级列表Adatper
    private lateinit var classListParentAdapter: ClassListParentAdapter
    // Adapter 空页面
    private lateinit var emptyView: View
    private lateinit var tvClassEmptyAdd: TextView
    // 撤回班级弹窗
    private lateinit var cancelJoinClassDialog: Dialog
    private lateinit var tvClassListCancel: TextView
    private lateinit var tvClassListConfirm: TextView
    // 添加 底部弹窗
    private lateinit var addChildrenBottomDialog: CustomCancelBottomDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_class_list_parent, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initView() {
        mPresenter = ClassListParentPresenter(context!!)
        mPresenter.mView = this
        userId = AppPrefsUtils.getInt("userId")
        // 添加
        headerBarClassList.getRightView()
            .setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        headerBarClassList.getRightView().text = "添加"
        headerBarClassList.getRightView().setVisible(true)
        // 配置Rv
        classListBean = arrayListOf()
        classListParentAdapter = ClassListParentAdapter(classListBean, false)
        rvClassListParent.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvClassListParent.adapter = classListParentAdapter
        // emptyView
        emptyView = LayoutInflater.from(context)
            .inflate(R.layout.layout_class_list_empty, rvClassListParent, false)
        tvClassEmptyAdd = emptyView.findViewById(R.id.tvClassEmptyAdd)
        classListParentAdapter.emptyView = emptyView
        // 撤回弹窗
        cancelJoinClassDialog = Dialog(context!!, R.style.BottomDialog)
        val subjectView =
            LayoutInflater.from(context!!).inflate(R.layout.class_list_cancel_dialog, null)
        cancelJoinClassDialog.setContentView(subjectView)
        initWindow(cancelJoinClassDialog)
        tvClassListCancel = subjectView.findViewById(R.id.tvClassListCancel)
        tvClassListConfirm = subjectView.findViewById(R.id.tvClassListConfirm)
    }

    private fun initWindow(dialog: Dialog) {
        val window = dialog.window
        val lp = window!!.attributes
        val d = window.windowManager.defaultDisplay
        lp.dimAmount = 0.6f
        lp.width = d.width
        window.attributes = lp
        window.setGravity(Gravity.CENTER)
        // 设置点击外围消散
        dialog.setCanceledOnTouchOutside(true)
    }

    private fun initData() {
        // 调用获取班级列表接口
        mPresenter.getClassListParent(userId, false)
    }

    private fun initListener() {
        // 添加
        headerBarClassList.getRightView().setOnClickListener {
            addChildrenBottomDialog = CustomCancelBottomDialog(activity!!, R.style.BottomDialog)
            addChildrenBottomDialog.addItem("选择孩子", R.color.common_black, View.OnClickListener { })
            // 添加孩子Item
            if (ChildrenInfoBean.childrenInfoBean.size > 0) {
                val bean = ChildrenInfoBean.childrenInfoBean
                for (item in bean) {
                    if (item.realName != null) {
                        // 班级状态 0:审核中 1：已通过
                        if (item.classState == null || item.classState == 2 || item.classState == -1) {
                            addChildrenBottomDialog.addChildrenItem(item.realName,
                                R.color.text_xdt,
                                item.avatar ?: "",
                                View.OnClickListener {
                                    val intent = Intent(context, JoinClassActivity::class.java)
                                    intent.putExtra("code", 2)
                                    intent.putExtra("studentId", item.studentId)
                                    intent.putExtra("childName", item.realName)
                                    intent.putExtra("relation", "")
                                    intent.putExtra("fromType","Main")
                                    startActivity(intent)
                                    addChildrenBottomDialog.dismiss()
                                })
                        }
                    }
                }
            }
            // 最下方添加孩子逻辑
            addChildrenBottomDialog.addChildItem(
                "添加孩子",
                R.color.login_xdt_btn_color_able,
                View.OnClickListener {
                    startActivityForResult(Intent(context!!, AddChildActivity::class.java), 10002)
                    addChildrenBottomDialog.dismiss()
                })
            addChildrenBottomDialog.show()
        }
        // 空页面的添加
        tvClassEmptyAdd.setOnClickListener {
            addChildrenBottomDialog = CustomCancelBottomDialog(activity!!, R.style.BottomDialog)
            addChildrenBottomDialog.addItem("选择孩子", R.color.common_black, View.OnClickListener { })
            // 添加孩子Item
            if (ChildrenInfoBean.childrenInfoBean.size > 0) {
                val bean = ChildrenInfoBean.childrenInfoBean
                for (item in bean) {
                    if (item.realName != null) {
                        // 班级状态 0:审核中 1：已通过
                        if (item.classState == null || item.classState == 2 || item.classState == -1) {
                            addChildrenBottomDialog.addChildrenItem(item.realName,
                                R.color.text_xdt,
                                item.avatar ?: "",
                                View.OnClickListener {
                                    val intent = Intent(context, JoinClassActivity::class.java)
                                    intent.putExtra("code", 2)
                                    intent.putExtra("studentId", item.studentId)
                                    intent.putExtra("childName", item.realName)
                                    intent.putExtra("relation", "")
                                    intent.putExtra("fromType","Main")
                                    startActivity(intent)
                                    addChildrenBottomDialog.dismiss()
                                })
                        }
                    }
                }
            }
            // 最下方添加孩子逻辑
            addChildrenBottomDialog.addChildItem(
                "添加孩子",
                R.color.login_xdt_btn_color_able,
                View.OnClickListener {
                    startActivityForResult(Intent(context!!, AddChildActivity::class.java), 10002)
                    addChildrenBottomDialog.dismiss()
                })
            addChildrenBottomDialog.show()
        }
        // Adapter
        classListParentAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                when (view?.id) {
                    R.id.rlClassDetail -> {
                        if (classListBean[position].state == 1) {
                            // 记录用户选择的classID  studentId
                            AppPrefsUtils.putInt("ParentClassId",classListBean[position].classId)
                            AppPrefsUtils.putInt("ParentStudentId",classListBean[position].studentId)
                            BaseRxBus.mBusInstance.post(EventClassListBean(classListBean[position]))
                        }
                    }
                    R.id.llClassNotice -> {
                        // 通知
                        val intent = Intent(context, NoticeParentListActivity::class.java)
                        intent.putExtra("classId",classListBean[position].classId)
                        startActivity(intent)
                    }
                    R.id.llClassContactTeacher -> {
                        // 拨打班主任电话
                        CallPhoneUtils.callPhone(context!!,classListBean[position].headerTeacherPhone)
                    }
                    R.id.tvCancelJoin -> {
                        cancelJoinClassDialog.show()
                        // 撤回加入班级
                        tvClassListCancel.setOnClickListener { cancelJoinClassDialog.dismiss() }
                        tvClassListConfirm.setOnClickListener {
                            // 调用撤回接口
                            mPresenter.cancelClassListParent(
                                classListBean[position].classId,
                                classListBean[position].studentId,
                                userId
                            )
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                10002 -> {
                    // (发送事件  让我的页面 更新孩子信息接口)
                    BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
                }
            }
        }
    }

    // 获取班级列表回调
    override fun getClassListParent(bean: MutableList<ParentClassListBean>?) {
        classListBean.clear()
        if (bean != null && bean.size > 0) {
            tvMyClass.setVisible(false)
            classListBean.addAll(bean)
        } else {
            tvMyClass.setVisible(false)
        }
        classListParentAdapter.notifyDataSetChanged()
    }

    // 撤回加入班级回调
    override fun cancelClassList(msg: String) {
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
        cancelJoinClassDialog.dismiss()
        mPresenter.getClassListParent(userId, true)
        ToastUtils.showMsg(context!!, msg)
    }
}