package com.kapok.apps.maple.xdt.classlist.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.activity_class_name.*

/**
 *  班级名称修改页
 *  fanjie
 */
class ClassNameActivity : BaseActivity() {
    private var hasName: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_name)
        initListener()
        initData()
    }

    private fun initData() {
        // 完成
        headerBarClassName.getRightView().setTextColor(resources.getColor(R.color.text_xdt_hint))
        headerBarClassName.getRightView().text = "完成"
        headerBarClassName.getRightView().setVisible(true)
    }

    private fun initListener() {
        // 班级名称监听
        etClassName.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    ivClassName.setVisible(true)
                } else {
                    ivClassName.setVisible(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                hasName = s.toString().isNotEmpty()
                if (hasName) {
                    headerBarClassName.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                } else {
                    headerBarClassName.getRightView().setTextColor(resources.getColor(R.color.text_xdt_hint))
                }
            }
        })
        // 清空手机号
        ivClassName.setOnClickListener { etClassName.text.clear() }
        // 完成
        headerBarClassName.getRightView().setOnClickListener {
            if (hasName) {
                val intent = Intent()
                intent.putExtra("className",etClassName.text.toString())
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
    }
}