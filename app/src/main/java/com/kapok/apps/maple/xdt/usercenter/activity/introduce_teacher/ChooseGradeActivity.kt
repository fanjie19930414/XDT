package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.adapter.ChooseGradeAdapter
import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kapok.apps.maple.xdt.usercenter.presenter.ChooseGradePresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.ChooseGradeView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CustomEmptyView
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.utils.Dp2pxUtils
import kotlinx.android.synthetic.main.activity_choose_grade.*

/**
 *  选择年级页面
 *  fanjie
 */
class ChooseGradeActivity : BaseMVPActivity<ChooseGradePresenter>(), ChooseGradeView {
    private lateinit var chooseGradeAdapter: ChooseGradeAdapter
    // 年级列表
    private lateinit var gradeList: ArrayList<GradeListBean>
    private lateinit var gradeListString: ArrayList<String>
    // 传入的Grade
    private var selectGrade: String = ""
    private var selectGradeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_grade)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = ChooseGradePresenter(this)
        mPresenter.mView = this
        initData()
    }

    private fun initData() {
        val intent = intent
        // 已选中的
        selectGrade = intent.getStringExtra("selectGrade")
        selectGradeId = intent.getIntExtra("selectGradeId", -1)
        gradeList = arrayListOf()
        gradeListString = arrayListOf()
        // 配置RecyclerView
        chooseGradeAdapter = ChooseGradeAdapter(this, gradeListString, selectGrade)
        rvGrade.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvGrade.adapter = chooseGradeAdapter
        rvGrade.addItemDecoration(RecycleViewDivider(this, RecycleViewDivider.VERTICAL, Dp2pxUtils.dp2px(this, 1)))
        chooseGradeAdapter.emptyView = CustomEmptyView.builder(this).setImgRes(R.drawable.icon_state_error)
        mPresenter.getRelationList()
    }

    private fun initListener() {
        // Adapter 点击事件
        chooseGradeAdapter.setOnItemClickListener { adapter, _, position ->
            val selectItem = adapter.data[position] as String
            val intent = Intent()
            intent.putExtra("selectItem", selectItem)
            intent.putExtra("selectItemId", gradeList[position].gradeId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        // 返回
        headerBarGrade.getLeftView().setOnClickListener {
            val intent = Intent()
            intent.putExtra("selectItem", selectGrade)
            intent.putExtra("selectItemId", selectGradeId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("selectItem", selectGrade)
        intent.putExtra("selectItemId", selectGradeId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // 获取年级列表回调
    override fun getGradeList(dataList: MutableList<GradeListBean>?) {
        if (dataList != null && dataList.size > 0) {
            gradeList.clear()
            gradeList.addAll(dataList)
            for (item in gradeList) {
                gradeListString.add(item.gradeName)
            }
            chooseGradeAdapter.notifyDataSetChanged()
        }
    }
}