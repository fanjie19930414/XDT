package com.kapok.apps.maple.xdt.home.fragment.fragment_teacher

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.SuggestReportActivity
import com.kapok.apps.maple.xdt.home.activity.UserInfoActivity
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.presenter.MyPresenterTeacher
import com.kapok.apps.maple.xdt.home.presenter.view.MyViewTeacher
import com.kapok.apps.maple.xdt.homework.activity.HomeWorkTeacherListActivity
import com.kapok.apps.maple.xdt.homework.activity.SendHomeWorkActivity
import com.kapok.apps.maple.xdt.notice.activity.NoticeTeacherListActivity
import com.kapok.apps.maple.xdt.timetable.activity.timetable_parent.TimeTableParentActivity
import com.kapok.apps.maple.xdt.timetable.activity.timetable_teacher.TimeTableTeacherActivity
import com.kapok.apps.maple.xdt.usercenter.activity.login.LoginForgetPWDActivity
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.GlideUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_my_parent.*
import kotlinx.android.synthetic.main.fragment_my_teacher.*

/**
 * 我的Fragment
 */
class MyFragmentTeacher : BaseMvpFragment<MyPresenterTeacher>(), MyViewTeacher {
    // 事件
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_teacher, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = MyPresenterTeacher(context!!)
        mPresenter.mView = this
        // 判断当前时间
        DateUtils.getMorningOrNoon(tvUserDescTeacher)
        // 订阅事件
        disposable = BaseRxBus.mBusInstance.toObservable(EventChildrenUserInfoMsg::class.java)
            .subscribe {
                mPresenter.getUserInfo(
                    AppPrefsUtils.getInt("userId"),
                    AppPrefsUtils.getString("identity").toInt()
                )
            }
    }

    private fun initListener() {
        rlUserInfoTeacher.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    UserInfoActivity::class.java
                )
            )
        }
        // 重置密码
        rlChangePWDTeacher.setOnClickListener { startActivity(Intent(activity, LoginForgetPWDActivity::class.java)) }
        // 通知
        llNotice.setOnClickListener {
            val intent = Intent(activity, NoticeTeacherListActivity::class.java)
            intent.putExtra("classId",0)
            intent.putExtra("isHeaderTeacher",false)
            intent.putExtra("from",true)
            startActivity(intent)
        }
        // 作业
        llHomeWork.setOnClickListener {
            val intent = Intent(activity, HomeWorkTeacherListActivity::class.java)
            intent.putExtra("classId",0)
            intent.putExtra("isHeaderTeacher", false)
            intent.putExtra("from", true)
            startActivity(intent)
        }
        // 意见反馈
        rlSuggestionTeacher.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    SuggestReportActivity::class.java
                )
            )
        }
        // 关于学点通（这里测试 先跳到 老师课程表页）
        rlAboutTeacher.setOnClickListener { }
    }

    override fun getUserInfoBean(bean: UserInfoBean) {
        // 获取用户名头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            Glide.with(this).load(bean.avatar).into(civIconTeacher)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civIconTeacher)
        }
        // 获取用户名
        tvUserNameTeacher.text = bean.realName
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseRxBus.mBusInstance.unSubscribe(disposable)
    }
}