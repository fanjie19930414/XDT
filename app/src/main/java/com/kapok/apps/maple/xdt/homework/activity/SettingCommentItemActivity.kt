package com.kapok.apps.maple.xdt.homework.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.SettingCommentAdapter
import com.kapok.apps.maple.xdt.homework.adapter.TeacherCommentAdapter
import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kapok.apps.maple.xdt.homework.presenter.SettingCommentPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.SettingCommentView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.account_login_activity.*
import kotlinx.android.synthetic.main.activity_setting_comment.*
import kotlinx.android.synthetic.main.activity_setting_comment_item.*
import kotlinx.android.synthetic.main.activity_student_answer.*

/**
 * 设置常用评语页
 * fanjie
 */
@SuppressLint("SetTextI18n")
class SettingCommentItemActivity : BaseMVPActivity<SettingCommentPresenter>(), SettingCommentView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_comment_item)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = SettingCommentPresenter(this)
        mPresenter.mView = this
    }

    private fun initListener() {
        btSaveComment.setOnClickListener {
            mPresenter.createCommonComment(
                etCommentItem.text.toString(),
                AppPrefsUtils.getInt("userId")
            )
        }
        etCommentItem.addTextChangedListener(object : DefaultTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvCommentLength.text = s?.length.toString() + "/20"
            }

            override fun afterTextChanged(s: Editable?) {
                val hasContent = s.toString().isNotEmpty()
                btSaveComment.isEnabled = hasContent
            }
        })
    }

    // 获取常用评论回调 (本页面无用)
    override fun getCommonComment(list: MutableList<CommonComment>?) {

    }

    override fun createComment(msg: String) {
        ToastUtils.showMsg(this, msg)
        etCommentItem.text = SpannableStringBuilder("")
    }
}