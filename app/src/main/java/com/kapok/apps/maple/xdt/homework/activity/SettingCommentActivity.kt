package com.kapok.apps.maple.xdt.homework.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.Dp2pxUtils
import kotlinx.android.synthetic.main.activity_join_class.*
import kotlinx.android.synthetic.main.activity_setting_comment.*
import kotlinx.android.synthetic.main.activity_student_answer.*

/**
 * 设置常用评语页
 * fanjie
 */
class SettingCommentActivity : BaseMVPActivity<SettingCommentPresenter>(), SettingCommentView {
    private lateinit var dataList: MutableList<CommonComment>
    private lateinit var commentAdapter: SettingCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_comment)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = SettingCommentPresenter(this)
        mPresenter.mView = this
        // 配置评论Rv
        dataList = arrayListOf()
        commentAdapter = SettingCommentAdapter(dataList)
        rvSettingComment.adapter = commentAdapter
        rvSettingComment.layoutManager = LinearLayoutManager(this)
        rvSettingComment.isNestedScrollingEnabled = false
        // Divider
        rvSettingComment.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(this)
            .inflate(R.layout.layout_class_list_empty3, rvSettingComment, false)
        commentAdapter.emptyView = emptyView
    }

    override fun onResume() {
        super.onResume()
        // 调用常用评论接口
        mPresenter.getCommonComment(AppPrefsUtils.getInt("userId"))
    }

    private fun initListener() {
        // 添加新的常用语
        tvAddNewSubject.setOnClickListener {
            startActivity(
                Intent(
                    this@SettingCommentActivity,
                    SettingCommentItemActivity::class.java
                )
            )
        }
        // Adapter 点击事件
        commentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                val intent = Intent()
                intent.putExtra("comment", dataList[position].content)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
    }

    // 获取常用评论回调
    override fun getCommonComment(list: MutableList<CommonComment>?) {
        if (list != null && list.size > 0) {
            dataList.clear()
            dataList.addAll(list)
        }
        commentAdapter.notifyDataSetChanged()
    }

    // 创建评语(本页面无用)
    override fun createComment(msg: String) {

    }
}