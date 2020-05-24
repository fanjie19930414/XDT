package com.kapok.apps.maple.xdt.usercenter.activity.login

import android.content.Intent
import android.os.Bundle
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kapok.apps.maple.xdt.usercenter.activity.IntroduceActivity
import com.kapok.apps.maple.xdt.usercenter.bean.LoginByCodeBean
import com.kapok.apps.maple.xdt.usercenter.presenter.LoginByCodePresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.LoginByCodeView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_verificode.*

/**
 *  登录页 (获取验证码页)
 *  fanjie
 */
class LoginVerifyCodeActivity : BaseMVPActivity<LoginByCodePresenter>(), LoginByCodeView {
    private lateinit var userMobile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificode)
        initView()
        initListener()
    }

    private fun initListener() {
        //验证码输入完的监听
        verificationcodeview.setOnCodeFinishListener { content: String? ->
            mPresenter.loginByCode(content.toString(), userMobile)
        }
        //重新获取验证码倒计时
        bt_verifycode.setOnClickListener {
            bt_verifycode.removeRunable()
            bt_verifycode.requestSendVerifyNumber()
            // 重新调用获取验证码接口
            mPresenter.loginMobile(userMobile)
        }
    }

    private fun initView() {
        mPresenter = LoginByCodePresenter(this)
        mPresenter.mView = this
        // 数据配置
        userMobile = intent.getStringExtra("mobile")
        tv_verifycode_mobile.text = userMobile
        bt_verifycode.text = "60秒 "
        bt_verifycode.setTextColor(resources.getColor(R.color.text_xdt_hint))
        bt_verifycode.requestSendVerifyNumber()
    }

    override fun onDestroy() {
        super.onDestroy()
        bt_verifycode.removeRunable()
    }

    // 输入验证码后的接口回调信息
    override fun loginByCodeResult(bean: LoginByCodeBean) {
        // 如果有身份 直接进入首页
        if (bean.identityType.isNullOrEmpty()) {
            // 将接口返回的信息存到本地SP中
            if (!bean.token.isNullOrEmpty()) {
                AppPrefsUtils.putString("token", bean.token)
                BaseUserInfo.token = bean.token
            }
            if (bean.userId != null) {
                AppPrefsUtils.putInt("userId", bean.userId)
                BaseUserInfo.userId = bean.userId
            }
            if (!bean.identityType.isNullOrEmpty()) {
                AppPrefsUtils.putString("identity",bean.identityType)
                BaseUserInfo.identity = bean.identityType.toInt()
            }
            if (!bean.realName.isNullOrEmpty()) {
                AppPrefsUtils.putString("userName", bean.realName)
                BaseUserInfo.userName = bean.realName
            }
            startActivity(Intent(this@LoginVerifyCodeActivity, IntroduceActivity::class.java))
            AppManager.instance.finishActivity(this)
        } else {
            // 将接口返回的信息存到本地SP中
            if (!bean.token.isNullOrEmpty()) {
                AppPrefsUtils.putString("token", bean.token)
                BaseUserInfo.token = bean.token
            }
            if (bean.userId != null) {
                AppPrefsUtils.putInt("userId", bean.userId)
                BaseUserInfo.userId = bean.userId
            }
            if (!bean.identityType.isNullOrEmpty()) {
                AppPrefsUtils.putString("identity",bean.identityType)
                BaseUserInfo.identity = bean.identityType.toInt()
            }
            if (!bean.realName.isNullOrEmpty()) {
                AppPrefsUtils.putString("userName", bean.realName)
                BaseUserInfo.userName = bean.realName
            }
            startActivity(Intent(this@LoginVerifyCodeActivity, MainActivity::class.java))
            AppManager.instance.finishAllActivity()
        }
    }

    // 重新获取验证码的监听
    override fun getReResult(isSuccess: Boolean) {
        ToastUtils.showMsg(this, "验证码已发送 请注意查收")
    }

}