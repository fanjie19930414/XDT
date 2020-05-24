package com.kapok.apps.maple.xdt.usercenter.activity.introduce_student

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kapok.apps.maple.xdt.usercenter.adapter.JoinClassAdapter
import com.kapok.apps.maple.xdt.usercenter.bean.JoinClassBean
import com.kapok.apps.maple.xdt.usercenter.presenter.JoinClassPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.JoinClassView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_join_class.*

/**
 *  加入班级类
 *  fanjie
 */
class JoinClassActivity : BaseMVPActivity<JoinClassPresenter>(), JoinClassView {
    private var hasClassNumber: Boolean = false
    // 加入班级Adapter
    private lateinit var mJoinClassAdapter: JoinClassAdapter
    // 加入班级列表
    private lateinit var mJoinClassList: ArrayList<JoinClassBean>
    // 身份   1:老师 2：学生
    private var code = -1
    private var studentId = -1
    private lateinit var childName: String
    private lateinit var relation: String
    // bottomDialog
    private lateinit var dialog: Dialog
    // 从哪跳转进来的
    private var fromType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_class)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = JoinClassPresenter(this@JoinClassActivity)
        mPresenter.mView = this
        // 获取传参
        val intent = intent
        code = intent.getIntExtra("code", -1)
        studentId = intent.getIntExtra("studentId",-1)
        childName = intent.getStringExtra("childName")
        relation = intent.getStringExtra("relation")
        fromType = intent.getStringExtra("fromType")
        // 从主页面跳转过来的 不展示返回和隐藏
        if (fromType == "Main") {
            headerBar_joinClass.getLeftView().setVisible(false)
            // 关闭
            headerBar_joinClass.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
            headerBar_joinClass.getRightView().text = "关闭"
            headerBar_joinClass.getRightView().setVisible(true)
        } else {
            // 跳过
            headerBar_joinClass.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
            headerBar_joinClass.getRightView().text = "跳过"
            headerBar_joinClass.getRightView().setVisible(true)
        }
        // 配置Rv
        mJoinClassList = arrayListOf()
        mJoinClassAdapter = JoinClassAdapter(this, mJoinClassList)
        rv_joinclass.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_joinclass.adapter = mJoinClassAdapter
        rv_joinclass.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
    }

    private fun initListener() {
        // 跳过
        headerBar_joinClass.getRightView().setOnClickListener {
            // 目前测试 本地写死状态 id 为 1 是老师  2是家长
            if (code == 1) {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("id",1)
                startActivity(intent)
                AppManager.instance.finishActivity(this)
            } else {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("id",2)
                startActivity(intent)
                AppManager.instance.finishActivity(this)
            }
        }
        // 班级号 老师手机号 监听
        et_joinclass_number.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    iv_joinclass_delete.setVisible(true)
                } else {
                    iv_joinclass_delete.setVisible(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasClassNumber = s.toString().isNotEmpty()
                bt_joinclass.isEnabled = hasClassNumber
            }
        })
        // 清空输入框
        iv_joinclass_delete.setOnClickListener { et_joinclass_number.text.clear() }
        // 调用获取班级Bean的class
        bt_joinclass.setOnClickListener {
//            if (RegexUtils.checkClassNum(et_joinclass_number.text.toString()) || RegexUtils.checkMobileNum(
//                    et_joinclass_number.text.toString()
//                )
//            ) {
//                // 调用获取班级Bean的接口（目前测试 手机传参默认为4 才有值）
//                mPresenter.getClassBean("4", code, userId = AppPrefsUtils.getInt("userId"))
//            } else {
//                ToastUtils.showMsg(this, "请输入正确的班级号或手机号")
//            }
            mPresenter.getClassBean(et_joinclass_number.text.toString(), AppPrefsUtils.getString("identity").toInt(), userId = AppPrefsUtils.getInt("userId"))
        }
        // 申请加入
        mJoinClassAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.tvJoinClass -> {
                        initDialog(position, code)
                    }
                }
            }
    }

    // 底部Dialog弹框
    private fun initDialog(position: Int, code: Int) {
        val schoolName = mJoinClassList[position].schoolName
        val startYear = mJoinClassList[position].startYear
        val grade = mJoinClassList[position].grade
        val headerTeacher = mJoinClassList[position].headerTeacher
        val className = mJoinClassList[position].className

        dialog = Dialog(this, R.style.BottomDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_join_class, null)
        dialog.setContentView(view)
        val window = dialog.window
        val lp = window!!.attributes
        val d = window.windowManager.defaultDisplay
        lp.dimAmount = 0.3f
        lp.width = d.width
        window.attributes = lp
        window.setGravity(Gravity.BOTTOM)
        // 设置点击外围消散
        dialog.setCanceledOnTouchOutside(true)
        val dialogParentText = if (relation.isNotEmpty() && childName.isNotEmpty()) {
            "您将以" + relation + childName + "老师的身份加入班级，申请后，请等待班主任的审核"
        } else {
            "申请后，请等待班主任的审核"
        }
        val dialogTeacherText = if (relation.isNotEmpty() && childName.isNotEmpty()) {
            "您将以" + childName + relation + "的身份加入班级，申请后，请等待班主任的审核"
        } else {
            "申请后，请等待班主任的审核"
        }
        // Dialog数据
        if (code == 1) {
            view.findViewById<TextView>(R.id.tv_bottomdialog_joinClass_title).text = dialogTeacherText
        } else {
            view.findViewById<TextView>(R.id.tv_bottomdialog_joinClass_title).text = dialogParentText
        }
        view.findViewById<TextView>(R.id.tvBottomDialogSchoolName).text = schoolName
        view.findViewById<TextView>(R.id.tvBottomDialogClassName).text = startYear.toString() + "级" + grade + className
        view.findViewById<TextView>(R.id.tvBottomDialogTeacherName).text = headerTeacher
        // 取消
        view.findViewById<TextView>(R.id.tvBottomDialogCancel).setOnClickListener { dialog.dismiss() }
        // 申请
        view.findViewById<TextView>(R.id.tvBottomDialogJoin).setOnClickListener {
            if (code == 1) {
                mPresenter.teacherApplyJoinClass(
                    mJoinClassList[position].classId,
                    AppPrefsUtils.getInt("userId")
                )
            } else {
                mPresenter.parentApplayJoinClass(
                    mJoinClassList[position].classId,
                    AppPrefsUtils.getInt("userId"),
                    studentId
                )
            }
        }
        dialog.show()
    }

    // 返回班级列表回调
    override fun getClassItem(dataList: MutableList<JoinClassBean>?) {
        if (dataList != null && dataList.size > 0) {
            rl_joinclass_item.setVisible(true)
            mJoinClassList.clear()
            mJoinClassList.addAll(dataList)
        }
        mJoinClassAdapter.notifyDataSetChanged()
    }

    // 家长申请加入班级回调
    override fun parentApplyJoinClass(msg: String) {
        ToastUtils.showMsg(this, msg)
        dialog.dismiss()
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
        // 目前测试 本地写死状态 id 为 1 是老师  2是家长
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("id",2)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
    }

    // 老师申请加入班级回调
    override fun teacherApplyJoinClass(msg: String) {
        ToastUtils.showMsg(this, msg)
        dialog.dismiss()
        // 目前测试 本地写死状态 id 为 1 是老师  2是家长
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("id",1)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
    }

    override fun onBackPressed() {
        if (code == 1) {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("id",1)
            startActivity(intent)
            AppManager.instance.finishActivity(this)
        } else {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("id",2)
            startActivity(intent)
            AppManager.instance.finishActivity(this)
        }
    }
}