package com.kapok.apps.maple.xdt.usercenter.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_student.EditInfoActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher.TeacherEditInfoActivity
import com.kapok.apps.maple.xdt.usercenter.activity.login.LoginMobileActivity
import com.kapok.apps.maple.xdt.usercenter.presenter.SplashPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SplahView
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import kotlinx.android.synthetic.main.activity_introduce.*
import kotlinx.android.synthetic.main.activity_splash.*

/**
 *  引导页(返回跳转登陆页)
 *  fanjie
 */
@SuppressLint("SetTextI18n")
class SplashActivity : BaseMVPActivity<SplashPresenter>(), SplahView {
    private val mHandler: Handler = Handler()
    // 3s自动关闭跳转首页
    private var mCount = 3
    private val countDown = object : Runnable {
        override fun run() {
            tvSplashPass.text = "跳过($mCount)"
            if (mCount > 0) {
                mHandler.postDelayed(this, 1000)
            } else {
                // 判断是否已经登陆()
                if (AppPrefsUtils.getInt("userId") != -1 && AppPrefsUtils.getString("identity").isNotEmpty()) {
                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                } else if (AppPrefsUtils.getInt("userId") != -1 && AppPrefsUtils.getString("identity").isEmpty()) {
                    startActivity(Intent(this@SplashActivity,IntroduceActivity::class.java))
                } else if (AppPrefsUtils.getInt("userId") == -1 && AppPrefsUtils.getString("identity").isEmpty()) {
                    startActivity(Intent(this@SplashActivity,LoginMobileActivity::class.java))
                }
                AppManager.instance.finishActivity(this@SplashActivity)
            }
            mCount--
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = SplashPresenter(this)
        mPresenter.mView = this
        if (AppPrefsUtils.getInt("userId") != -1 && AppPrefsUtils.getString("identity").isNotEmpty()) {
            mPresenter.getUserInfo(AppPrefsUtils.getInt("userId"),AppPrefsUtils.getString("identity").toInt(),false)
        } else {
            mHandler.postDelayed(countDown,0)
        }
    }

    private fun initListener() {
        tvSplashPass.setOnClickListener {
            // 判断是否已经登陆()
            if (AppPrefsUtils.getInt("userId") != -1 && AppPrefsUtils.getString("identity").isNotEmpty()) {
                startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            } else if (AppPrefsUtils.getInt("userId") != -1 && AppPrefsUtils.getString("identity").isEmpty()) {
                startActivity(Intent(this@SplashActivity,IntroduceActivity::class.java))
            } else if (AppPrefsUtils.getInt("userId") == -1 && AppPrefsUtils.getString("identity").isEmpty()) {
                startActivity(Intent(this@SplashActivity,LoginMobileActivity::class.java))
            }
            AppManager.instance.finishActivity(this)
        }
    }

    override fun getUserInfoBean(bean: UserInfoBean) {
        tvSplashPass.setVisible(true)
        // 将接口返回的信息存到本地SP中
        AppPrefsUtils.putInt("userId", bean.userId)
        BaseUserInfo.userId = bean.userId
        // 加上身份不为Null 判定
        if (bean.identityType != null) {
            AppPrefsUtils.putString("identity",bean.identityType.toString())
            BaseUserInfo.identity = bean.identityType
        } else {
            AppPrefsUtils.putString("identity","")
        }
        if (bean.realName.isNotEmpty()) {
            AppPrefsUtils.putString("userName", bean.realName)
            BaseUserInfo.userName = bean.realName
        }
        mHandler.postDelayed(countDown,0)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(countDown)
    }
}