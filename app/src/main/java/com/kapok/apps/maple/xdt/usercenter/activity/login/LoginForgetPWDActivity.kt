package com.kapok.apps.maple.xdt.usercenter.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.presenter.LoginForgetPWDPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.BooleanView
import com.kapok.apps.maple.xdt.usercenter.presenter.view.LoginForgetPWDView
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.utils.RegexUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.forget_pwd_activity.*

/**
 *  登录页 (忘记密码)
 *  fanjie
 */
class LoginForgetPWDActivity : BaseMVPActivity<LoginForgetPWDPresenter>(), LoginForgetPWDView {
    private var hasMobile: Boolean = false
    private var hasVerifyCode: Boolean = false
    private var hasPwd: Boolean = false
    private var hideEye: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_pwd_activity)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = LoginForgetPWDPresenter(this)
        mPresenter.mView = this
        // 配置
        bt_verify_forget.text = "获取验证码"
        bt_verify_forget.isEnabled = false
        headerBar.setImageResourceLeft(R.mipmap.pop_right_del)
    }

    private fun initListener() {
        // 输入手机号监听
        et_login_forget_mobile.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    view_login_forget_mobile.setBackgroundColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    view_login_forget_mobile.setBackgroundColor(resources.getColor(R.color.line_gray))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                bt_verify_forget.isEnabled = RegexUtils.checkMobileNum(s.toString())
                if (bt_verify_forget.isEnabled) {
                    bt_verify_forget.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    bt_verify_forget.setTextColor(resources.getColor(R.color.text_xdt_hint))
                }
                hasMobile = s.toString().isNotEmpty()
                bt_login_forget.isEnabled = hasMobile && hasPwd && hasVerifyCode
            }
        })
        // 获取验证码
        bt_verify_forget.setOnClickListener {
            bt_verify_forget.requestSendVerifyNumber()
            mPresenter.sendCodeForPWD(et_login_forget_mobile.text.toString().trim())
        }
        // 验证码监听
        et_login_forget_verifycode.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    view_login_forget_verifycode.setBackgroundColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    view_login_forget_verifycode.setBackgroundColor(resources.getColor(R.color.line_gray))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasVerifyCode = s.toString().isNotEmpty()
                bt_login_forget.isEnabled = hasMobile && hasPwd && hasVerifyCode
            }
        })
        // 新密码监听
        et_login_forget_pwd.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    view_login_forget_pwd.setBackgroundColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    view_login_forget_pwd.setBackgroundColor(resources.getColor(R.color.line_gray))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasPwd = s.toString().isNotEmpty()
                bt_login_forget.isEnabled = hasMobile && hasPwd && hasVerifyCode
            }
        })
        // 调用修改密码接口
        bt_login_forget.setOnClickListener {
            if (RegexUtils.checkPassWord(et_login_forget_pwd.text.toString())) {
                mPresenter.findPWD(
                    et_login_forget_verifycode.text.toString().trim(),
                    et_login_forget_pwd.text.toString().trim(),
                    et_login_forget_mobile.text.toString().trim()
                )
            } else {
                ToastUtils.showMsg(this, "新密码需8-16同时包含数字和字母的组合")
            }
        }
        // 快捷登录
        tv_login_forget_quick.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@LoginForgetPWDActivity, LoginMobileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 是否显示密码
        iv_login_forget_eye.setOnClickListener {
            if (!hideEye) {
                hideEye = true
                iv_login_forget_eye.setBackgroundResource(R.mipmap.login_hidden)
                et_login_forget_pwd.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                et_login_forget_pwd.setSelection(et_login_forget_pwd.text.toString().length)
            } else {
                hideEye = false
                iv_login_forget_eye.setBackgroundResource(R.mipmap.login_visible)
                et_login_forget_pwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                et_login_forget_pwd.setSelection(et_login_forget_pwd.text.toString().length)
            }
        }
    }

    // 获取验证码回调
    override fun getCodeResult(isSuccess: Boolean) {
        ToastUtils.showMsg(this, "验证码已发送 请注意查收")
    }

    // 修改密码提交回调
    override fun changePWEResult(isSuccess: Boolean) {
        ToastUtils.showMsg(this, "密码修改成功")
        finish()
    }
}