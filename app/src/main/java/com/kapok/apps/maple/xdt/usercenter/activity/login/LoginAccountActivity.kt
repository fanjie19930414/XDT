package com.kapok.apps.maple.xdt.usercenter.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kapok.apps.maple.xdt.usercenter.activity.IntroduceActivity
import com.kapok.apps.maple.xdt.usercenter.bean.LoginByCodeBean
import com.kapok.apps.maple.xdt.usercenter.presenter.LoginAccountPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.LoginAccountView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.RegexUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.account_login_activity.*

/**
 *  登录页 (账号密码)
 *  fanjie
 */
class LoginAccountActivity : BaseMVPActivity<LoginAccountPresenter>(), LoginAccountView {
    private var hasMobile: Boolean = false
    private var hasPwd: Boolean = false
    private var hideEye: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_login_activity)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = LoginAccountPresenter(this)
        mPresenter.mView = this
    }

    private fun initListener() {
        // 输入手机号监听
        et_login_account_mobile.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    view_login_account_mobile.setBackgroundColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    iv_login_account_del.setVisible(true)
                } else {
                    view_login_account_mobile.setBackgroundColor(resources.getColor(R.color.line_gray))
                    iv_login_account_del.setVisible(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasMobile = s.toString().isNotEmpty()
                bt_login_account.isEnabled = hasMobile && hasPwd
            }
        })
        // 清空手机号
        iv_login_account_del.setOnClickListener { et_login_account_mobile.text.clear() }
        // 输入密码监听
        et_login_account_pwd.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    view_login_account_pwd.setBackgroundColor(resources.getColor(R.color.login_xdt_btn_color_able))
                    iv_login_account_del_pwd.setVisible(true)
                } else {
                    view_login_account_pwd.setBackgroundColor(resources.getColor(R.color.line_gray))
                    iv_login_account_del_pwd.setVisible(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasPwd = s.toString().isNotEmpty()
                bt_login_account.isEnabled = hasMobile && hasPwd
            }
        })
        // 清空密码监听
        iv_login_account_del_pwd.setOnClickListener { et_login_account_pwd.text.clear() }
        // 调用登录接口
        bt_login_account.setOnClickListener {
            if (RegexUtils.checkMobileNum(et_login_account_mobile.text.toString())) {
                // 调用 手机号 登陆逻辑接口
                mPresenter.loginByPassWord(
                    et_login_account_mobile.text.toString(),
                    et_login_account_pwd.text.toString()
                )
            } else {
                ToastUtils.showMsg(this,"请输入正确的手机号")
            }
        }
        // 快捷登录
        tv_login_quick.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@LoginAccountActivity, LoginMobileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 忘记密码
        tv_login_forget.setOnClickListener {
            // 跳转到忘记密码页
            startActivity(Intent(this, LoginForgetPWDActivity::class.java))
        }
        // 链接Web页面
        tv_login_web_account.setOnClickListener {
            // 跳转Web页面隐私链接
        }
        // 是否显示密码
        iv_login_account_eye.setOnClickListener {
            if (!hideEye) {
                hideEye = true
                iv_login_account_eye.setImageResource(R.mipmap.login_hidden)
                et_login_account_pwd.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                et_login_account_pwd.setSelection(et_login_account_pwd.text.toString().length)
            } else {
                hideEye = false
                iv_login_account_eye.setImageResource(R.mipmap.login_visible)
                et_login_account_pwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                et_login_account_pwd.setSelection(et_login_account_pwd.text.toString().length)
            }
        }
    }

    override fun loginResult(bean: LoginByCodeBean) {
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
            startActivity(Intent(this@LoginAccountActivity, IntroduceActivity::class.java))
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
            startActivity(Intent(this@LoginAccountActivity,MainActivity::class.java))
            AppManager.instance.finishAllActivity()
        }
    }
}