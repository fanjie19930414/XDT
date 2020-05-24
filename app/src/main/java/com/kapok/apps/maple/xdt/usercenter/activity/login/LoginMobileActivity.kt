package com.kapok.apps.maple.xdt.usercenter.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.presenter.LoginMobilePresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.BooleanView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.RegexUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_login_mobile.*

/**
 *  登录页 (手机登陆)
 *  fanjie
 */
class LoginMobileActivity : BaseMVPActivity<LoginMobilePresenter>(), BooleanView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_mobile)
        initView()
        setListener()
    }

    private fun initView() {
        mPresenter = LoginMobilePresenter(this)
        mPresenter.mView = this
    }

    private fun setListener() {
        // 输入手机号监听
        et_login_mobile.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    iv_login_mobile_del.setVisible(true)
                    view_login_mobile.setBackgroundColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    iv_login_mobile_del.setVisible(false)
                    view_login_mobile.setBackgroundColor(resources.getColor(R.color.line_gray))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                bt_login_mobile.isEnabled = s.toString().isNotEmpty()
            }
        })
        // 清空手机号
        iv_login_mobile_del.setOnClickListener { et_login_mobile.text.clear() }
        // 获取验证码
        bt_login_mobile.setOnClickListener {
            if (RegexUtils.checkMobileNum(et_login_mobile.text.toString())) {
                // 调用 手机号 登陆逻辑接口
                mPresenter.loginMobile(et_login_mobile.text.toString())
            } else {
                ToastUtils.showMsg(this, "请输入正确的手机号")
            }
        }
        // 账号密码登录
        tv_login_other.setOnClickListener {
            startActivity(Intent(this, LoginAccountActivity::class.java))
        }
        // 链接Web页面
        tv_login_web.setOnClickListener {
            // 跳转Web页面隐私链接
            val intent = Intent()
            intent.putExtra("mobile", et_login_mobile.text.toString())
            intent.setClass(this, LoginVerifyCodeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getResult(result: Boolean) {
        // 跳转到验证码界面
        if (result) {
            ToastUtils.showMsg(this, "验证码已发送 请注意查收")
            val intent = Intent(this, LoginVerifyCodeActivity::class.java)
            intent.putExtra("mobile", et_login_mobile.text.toString())
            startActivity(intent)
        }
    }
}
