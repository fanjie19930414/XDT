package com.kapok.apps.maple.xdt.home.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.activity_edit_userinfo_name.*

/**
 *  个人信息/孩子信息 名称修改页
 *  fanjie
 */
class UserInfoEditActivity : BaseActivity() {
    private var hasName: Boolean = false
    private var isChildren: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_userinfo_name)
        initListener()
        initData()
    }

    private fun initData() {
        isChildren = intent.getBooleanExtra("isChild",false)
        if (isChildren) {
            etUserInfoName.hint = "请输入孩子的真实姓名"
        } else {
            etUserInfoName.hint = "请输入您的真实姓名"
        }
        // 完成
        headerBarUserInfoName.getRightView().setTextColor(resources.getColor(R.color.text_xdt_hint))
        headerBarUserInfoName.getRightView().text = "保存"
        headerBarUserInfoName.getRightView().setVisible(true)
    }

    private fun initListener() {
        // 姓名监听
        etUserInfoName.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    ivUserInfoName.setVisible(true)
                } else {
                    ivUserInfoName.setVisible(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasName = s.toString().isNotEmpty()
                if (hasName) {
                    headerBarUserInfoName.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    headerBarUserInfoName.getRightView().setTextColor(resources.getColor(R.color.text_xdt_hint))
                }
            }
        })
        // 清空
        ivUserInfoName.setOnClickListener { etUserInfoName.text.clear() }
        // 完成
        headerBarUserInfoName.getRightView().setOnClickListener {
            if (hasName) {
                val intent = Intent()
                intent.putExtra("EditName",etUserInfoName.text.toString())
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
    }
}