package com.kapok.apps.maple.xdt.home.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.activity_edit_userinfo_phone.*

/**
 *  个人信息电话修改页
 *  fanjie
 */
class UserInfoEditPhoneActivity : BaseActivity() {
    private var hasPhone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_userinfo_phone)
        initListener()
        initData()
    }

    private fun initData() {
        // 完成
        headerBarUserInfoPhone.getRightView().setTextColor(resources.getColor(R.color.text_xdt_hint))
        headerBarUserInfoPhone.getRightView().text = "保存"
        headerBarUserInfoPhone.getRightView().setVisible(true)
    }

    private fun initListener() {
        // 监听
        etUserInfoPhone.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    ivUserInfoPhone.setVisible(true)
                } else {
                    ivUserInfoPhone.setVisible(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasPhone = s.toString().isNotEmpty()
                if (hasPhone) {
                    headerBarUserInfoPhone.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    headerBarUserInfoPhone.getRightView().setTextColor(resources.getColor(R.color.text_xdt_hint))
                }
            }
        })
        // 清空
        ivUserInfoPhone.setOnClickListener { etUserInfoPhone.text.clear() }
        // 完成
        headerBarUserInfoPhone.getRightView().setOnClickListener {
            if (hasPhone) {
                val intent = Intent()
                intent.putExtra("EditPhone",etUserInfoPhone.text.toString())
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
    }
}