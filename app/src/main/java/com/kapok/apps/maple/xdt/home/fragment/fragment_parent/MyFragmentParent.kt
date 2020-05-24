package com.kapok.apps.maple.xdt.home.fragment.fragment_parent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.AddChildActivity
import com.kapok.apps.maple.xdt.home.activity.ChildrenUserInfoActivity
import com.kapok.apps.maple.xdt.home.activity.SuggestReportActivity
import com.kapok.apps.maple.xdt.home.activity.UserInfoActivity
import com.kapok.apps.maple.xdt.home.adapter.MyChildrenAdapter
import com.kapok.apps.maple.xdt.home.bean.MyChildrenBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.commen.ChildrenInfoBean
import com.kapok.apps.maple.xdt.home.presenter.MyPresenterParent
import com.kapok.apps.maple.xdt.home.presenter.view.MyViewParent
import com.kapok.apps.maple.xdt.homework.activity.HomeWorkParentListActivity
import com.kapok.apps.maple.xdt.notice.activity.NoticeParentListActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_student.JoinClassActivity
import com.kapok.apps.maple.xdt.usercenter.activity.login.LoginForgetPWDActivity
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_class_list_parent.*
import kotlinx.android.synthetic.main.fragment_my_parent.*
import kotlinx.android.synthetic.main.fragment_my_parent.tvUserName

/**
 * 我的Fragment
 */
@SuppressLint("InflateParams")
class MyFragmentParent : BaseMvpFragment<MyPresenterParent>(), MyViewParent {
    // 孩子集合
    private lateinit var childListBean: MutableList<MyChildrenBean>
    private lateinit var childAdapter: MyChildrenAdapter
    private lateinit var emptyView: View
    private lateinit var emptyViewAdd: TextView
    // 事件
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_parent, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = MyPresenterParent(context!!)
        mPresenter.mView = this
        // 判断当前时间
        DateUtils.getMorningOrNoon(tvUserDesc)
        // 配置Rv
        childListBean = arrayListOf()
        childAdapter = MyChildrenAdapter(context!!, childListBean)
        rvChildrenList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvChildrenList.adapter = childAdapter
        // emptyView
        emptyView = LayoutInflater.from(context)
            .inflate(R.layout.layout_class_list_empty, rvClassListParent, false)
        val tvEmptyContent = emptyView.findViewById<TextView>(R.id.tvEmptyContent)
        emptyViewAdd = emptyView.findViewById(R.id.tvClassEmptyAdd)
        tvEmptyContent.text = "还没有添加孩子哦"
        childAdapter.emptyView = emptyView
        // 调用获取孩子接口
        mPresenter.getSubjectSetting(AppPrefsUtils.getInt("userId"))
        // 订阅事件
        disposable = BaseRxBus.mBusInstance.toObservable(EventChildrenUserInfoMsg::class.java)
            .subscribe {
                mPresenter.getSubjectSetting(AppPrefsUtils.getInt("userId"))
                mPresenter.getUserInfo(
                    AppPrefsUtils.getInt("userId"),
                    AppPrefsUtils.getString("identity").toInt(),
                    true
                )
            }
    }

    private fun initListener() {
        // 详情
        rlInfoParent.setOnClickListener {
            startActivity(Intent(context, UserInfoActivity::class.java))
        }
        // 添加孩子
        btAddChild.setOnClickListener {
            startActivityForResult(Intent(context!!, AddChildActivity::class.java), 10001)
        }
        // 孩子列表点击
        childAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(context, ChildrenUserInfoActivity::class.java)
                intent.putExtra("studentId", childListBean[position].studentId)
                intent.putExtra("classState", childListBean[position].classState)
                intent.putExtra("classId", childListBean[position].classId)
                intent.putExtra("sex", childListBean[position].sex)
                startActivity(intent)
            }
        // 加入班级
        childAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                // 加入班级
                R.id.tvChildrenGoToJoined -> {
                    val intent = Intent(context, JoinClassActivity::class.java)
                    intent.putExtra("code", 2)
                    intent.putExtra("studentId", childListBean[position].studentId)
                    intent.putExtra("childName", childListBean[position].realName)
                    intent.putExtra("relation", "")
                    intent.putExtra("fromType", "Main")
                    startActivity(intent)
                }
                // 剩余作业
                R.id.llLeftHomeWork -> {
                    val intent = Intent(context, HomeWorkParentListActivity::class.java)
                    intent.putExtra("classId", childListBean[position].classId)
                    startActivity(intent)
                }
                // 剩余通知
                R.id.llLeftNotice -> {
                    val intent = Intent(context, NoticeParentListActivity::class.java)
                    intent.putExtra("classId", childListBean[position].classId)
                    startActivity(intent)
                }
            }
        }
        // 重置密码
        rlChangePWD.setOnClickListener { startActivity(Intent(activity, LoginForgetPWDActivity::class.java)) }
        // 意见反馈
        rlSuggestion.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    SuggestReportActivity::class.java
                )
            )
        }
        // 关于
        rlAbout.setOnClickListener { }
    }

    // 获取我的孩子回调
    override fun getMyChildren(bean: MutableList<MyChildrenBean>?) {
        childListBean.clear()
        // 清空孩子信息
        ChildrenInfoBean.childrenInfoBean.clear()
        if (bean != null && bean.size > 0) {
            childListBean.addAll(bean)
            ChildrenInfoBean.childrenInfoBean.addAll(bean)
        }
        childAdapter.notifyDataSetChanged()
    }

    // 用户信息回调
    override fun getUserInfoBean(bean: UserInfoBean) {
        // 获取用户名头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            Glide.with(this).load(bean.avatar).into(civIconParent)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civIconParent)
        }
        // 获取用户名
        tvUserName.text = bean.realName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                10001 -> {
                    // 调用获取孩子接口
                    mPresenter.getSubjectSetting(AppPrefsUtils.getInt("userId"))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseRxBus.mBusInstance.unSubscribe(disposable)
    }
}