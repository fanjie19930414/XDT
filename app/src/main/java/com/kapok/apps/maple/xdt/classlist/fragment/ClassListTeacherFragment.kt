package com.kapok.apps.maple.xdt.classlist.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.addressbook.activity.AddressBookTeacherActivity
import com.kapok.apps.maple.xdt.classlist.activity.ClassDetailTeacherActivity
import com.kapok.apps.maple.xdt.classlist.adapter.ClassListParentAdapter
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.classlist.presenter.ClassListTeacherPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassListParentView
import com.kapok.apps.maple.xdt.homework.activity.SendHomeWorkActivity
import com.kapok.apps.maple.xdt.notice.activity.NoticeTeacherListActivity
import com.kapok.apps.maple.xdt.notice.activity.SendNoticeActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_student.JoinClassActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher.CreateClassActivity
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventClassListBean
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_class_list_parent.rvClassListParent
import kotlinx.android.synthetic.main.fragment_class_list_teacher.*

/**
 * 班级列表老师端
 * fanjie
 */
class ClassListTeacherFragment : BaseMvpFragment<ClassListTeacherPresenter>(), ClassListParentView {
    private var userId: Int = -1
    // 班级列表集合
    private lateinit var classListBean: MutableList<ParentClassListBean>
    // 班级列表Adatper
    private lateinit var classListTeacherAdapter: ClassListParentAdapter
    // Adapter 空页面
    private lateinit var emptyView: View
    private lateinit var tvClassEmptyAdd: TextView
    // isLeader
    private var isLeader: Boolean = false
    // 底部创建班级/加入班级弹窗
    private lateinit var classListBottomDialog: CustomCancelBottomDialog
    // 撤回班级弹窗
    private lateinit var cancelJoinClassDialog: Dialog
    private lateinit var tvClassListCancel: TextView
    private lateinit var tvClassListConfirm: TextView
    // 底部弹窗Pop
    lateinit var pop: PopupWindow
    lateinit var parentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_class_list_teacher, null, false)
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
        mPresenter = ClassListTeacherPresenter(context!!)
        mPresenter.mView = this
        userId = AppPrefsUtils.getInt("userId")
        // 添加
        headerBarClassListTeacher.getRightView()
            .setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        headerBarClassListTeacher.getRightView().text = "添加"
        headerBarClassListTeacher.getRightView().setVisible(true)
        // 配置Rv
        classListBean = arrayListOf()
        classListTeacherAdapter = ClassListParentAdapter(classListBean, true)
        rvClassListTeacher.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvClassListTeacher.adapter = classListTeacherAdapter
        // emptyView
        emptyView = LayoutInflater.from(context)
            .inflate(R.layout.layout_class_list_empty, rvClassListParent, false)
        tvClassEmptyAdd = emptyView.findViewById(R.id.tvClassEmptyAdd)
        classListTeacherAdapter.emptyView = emptyView
        // 撤回弹窗
        cancelJoinClassDialog = Dialog(context!!, R.style.BottomDialog)
        val subjectView =
            LayoutInflater.from(context!!).inflate(R.layout.class_list_cancel_dialog, null)
        cancelJoinClassDialog.setContentView(subjectView)
        initWindow(cancelJoinClassDialog)
        tvClassListCancel = subjectView.findViewById(R.id.tvClassListCancel)
        tvClassListConfirm = subjectView.findViewById(R.id.tvClassListConfirm)
        // 底部Pop
        initPop()
    }

    private fun initPop() {
        pop = PopupWindow(context)
        parentView = LayoutInflater.from(context).inflate(R.layout.layout_class_list, null)
        pop.contentView = parentView
        pop.height = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.width = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.isTouchable = true
        pop.isFocusable = true
        pop.setBackgroundDrawable(resources.getDrawable(R.color.transparent))
        pop.isOutsideTouchable = true
        pop.update()
        pop.setOnDismissListener {
            val lp = activity!!.window.attributes
            lp.alpha = 1f
            activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            activity!!.window.attributes = lp
        }
    }

    // 设置显示在v上方（以v的中心位置为开始位置）
    private fun showUp2(v: View) {
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = parentView.measuredHeight
        val popupWidth = parentView.measuredWidth

        // 产生背景变暗效果
        val lp = activity!!.window.attributes
        lp.alpha = 0.6f
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity!!.window.attributes = lp
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
        // 调用获取班级列表接口(默认不勾选)
        mPresenter.getClassListTeacher(isLeader, userId)
    }

    private fun initListener() {
        // 添加
        headerBarClassListTeacher.getRightView().setOnClickListener {
            // dialog
            classListBottomDialog = CustomCancelBottomDialog(context!!, R.style.BottomDialog)
            classListBottomDialog.addItem(
                "创建班级",
                R.color.text_xdt,
                View.OnClickListener {
                    // 跳转到创建班级页
                    val intent = Intent()
                    intent.putExtra("selectSchool", AppPrefsUtils.getString("selectSchool"))
                    intent.putExtra("selectSchoolId", AppPrefsUtils.getInt("selectSchoolId"))
                    intent.setClass(context!!, CreateClassActivity::class.java)
                    startActivity(intent)
                    classListBottomDialog.dismiss()
                })
            classListBottomDialog.addItem(
                "加入班级",
                R.color.text_xdt,
                View.OnClickListener {
                    val intent = Intent(context, JoinClassActivity::class.java)
                    intent.putExtra("code", 1)
                    intent.putExtra("childName", "")
                    intent.putExtra("relation", "")
                    intent.putExtra("fromType", "Main")
                    startActivity(intent)
                    classListBottomDialog.dismiss()
                })
            classListBottomDialog.show()
        }
        // 创建班级 /  加入班级
        tvClassEmptyAdd.setOnClickListener {
            classListBottomDialog = CustomCancelBottomDialog(context!!, R.style.BottomDialog)
            classListBottomDialog.addItem(
                "创建班级",
                R.color.text_xdt,
                View.OnClickListener {
                    // 跳转到创建班级页
                    val intent = Intent()
                    intent.putExtra("selectSchool", AppPrefsUtils.getString("selectSchool"))
                    intent.putExtra("selectSchoolId", AppPrefsUtils.getInt("selectSchoolId"))
                    intent.setClass(context!!, CreateClassActivity::class.java)
                    startActivity(intent)
                    classListBottomDialog.dismiss()
                })
            classListBottomDialog.addItem(
                "加入班级",
                R.color.text_xdt,
                View.OnClickListener {
                    val intent = Intent(context, JoinClassActivity::class.java)
                    intent.putExtra("code", 1)
                    intent.putExtra("childName", "")
                    intent.putExtra("relation", "")
                    intent.putExtra("fromType", "Main")
                    startActivity(intent)
                    classListBottomDialog.dismiss()
                })
            classListBottomDialog.show()
        }
        // 只看我管理的班级
        ivClassListCheck.setOnClickListener {
            if (isLeader) {
                isLeader = false
                ivClassListCheck.setImageResource(R.mipmap.chk_box_off)
            } else {
                isLeader = true
                ivClassListCheck.setImageResource(R.mipmap.chk_box_on)
            }
            mPresenter.getClassListTeacher(isLeader, userId, true)
        }
        // Adapter
        classListTeacherAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view?.id) {
                    R.id.rlClassDetail -> {
                        if (classListBean[position].state == 1) {
                            // 记录用户选择的classID  studentId
                            AppPrefsUtils.putInt("TeacherClassId",classListBean[position].classId)
                            BaseRxBus.mBusInstance.post(EventClassListBean(classListBean[position]))
                        }
                    }
                    R.id.llClassNotice -> {
                        // 通知
                        val intent = Intent(context, NoticeTeacherListActivity::class.java)
                        intent.putExtra("classId",classListBean[position].classId)
                        intent.putExtra("isHeaderTeacher",AppPrefsUtils.getInt("userId") == classListBean[position].headerTeacherId)
                        intent.putExtra("from",false)
                        startActivity(intent)
                    }
                    R.id.llClassContactTeacher -> {
                        // 通讯录
                        val intent = Intent(context, AddressBookTeacherActivity::class.java)
                        intent.putExtra("classId",classListBean[position].classId)
                        intent.putExtra("isHeaderTeacher",AppPrefsUtils.getInt("userId") == classListBean[position].headerTeacherId)
                        startActivity(intent)
                    }
                    R.id.tvCancelJoin -> {
                        cancelJoinClassDialog.show()
                        // 撤回加入班级
                        tvClassListCancel.setOnClickListener { cancelJoinClassDialog.dismiss() }
                        tvClassListConfirm.setOnClickListener {
                            // 调用撤回接口
                            mPresenter.cancelJoinClassTeacher(
                                classListBean[position].classId,
                                classListBean[position].studentId,
                                userId
                            )
                        }
                    }
                }
            }
        classListTeacherAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                if (classListBean[position].state == 1) {
                    val intent = Intent(context, ClassDetailTeacherActivity::class.java)
                    intent.putExtra("classId", classListBean[position].classId)
                    startActivity(intent)
                }
            }
        // 底部Pop
        FABClassListTeacher.setOnClickListener { v -> showUp2(v) }
        parentView.findViewById<TextView>(R.id.tvClassNotice)
            .setOnClickListener {
                // 发通知
                val intent = Intent(activity, SendNoticeActivity::class.java)
                startActivity(intent)
                pop.dismiss()
            }
        parentView.findViewById<TextView>(R.id.tvClassHomeWork)
            .setOnClickListener {
                // 发作业
                val intent = Intent(activity, SendHomeWorkActivity::class.java)
                intent.putExtra("fromCheckHomeWork", false)
                startActivity(intent)
                pop.dismiss()
            }
        parentView.findViewById<TextView>(R.id.tvClassMoney)
            .setOnClickListener {
                ToastUtils.showMsg(context!!, "记账")
                pop.dismiss()
            }
    }

    // 获取班级列表回调
    override fun getClassListParent(bean: MutableList<ParentClassListBean>?) {
        classListBean.clear()
        if (bean != null && bean.size > 0) {
            rlClassListTeacher.setVisible(true)
            classListBean.addAll(bean)
        } else {
            rlClassListTeacher.setVisible(false)
        }
        classListTeacherAdapter.notifyDataSetChanged()
    }

    // 取消加入班级回调
    override fun cancelClassList(msg: String) {
        cancelJoinClassDialog.dismiss()
        mPresenter.getClassListTeacher(isLeader, userId, true)
        ToastUtils.showMsg(context!!, msg)
    }
}