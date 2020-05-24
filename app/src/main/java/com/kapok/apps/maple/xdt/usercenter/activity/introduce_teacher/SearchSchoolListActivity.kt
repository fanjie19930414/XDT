package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.adapter.SearchSchoolListAdapter
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SchoolListBean
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SearchSchoolListBean
import com.kapok.apps.maple.xdt.usercenter.presenter.SearchSchoolListPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SearchSchoolListView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.utils.Dp2pxUtils
import kotlinx.android.synthetic.main.activity_search_school_list.*

/**
 * 搜索学校列表页
 * fanjie
 */
class SearchSchoolListActivity : BaseMVPActivity<SearchSchoolListPresenter>(), SearchSchoolListView {
    // 当前选中的城市
    private lateinit var selectCity: String
    // 选中的学校
    private var selectSchool: String = ""
    private var selectSchoolId: Int = -1
    private lateinit var searchSchoolListAdapter: SearchSchoolListAdapter
    // 学校列表
    private lateinit var searchSchoolSchoolListBean: MutableList<SchoolListBean>
    private var pageIndex: Int = 1
    private val pageSize: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_school_list)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = SearchSchoolListPresenter(this)
        mPresenter.mView = this
        // 获取当前城市
        val intent = intent
        selectCity = intent.getStringExtra("cityName")
        // 配置RecyclerView
        searchSchoolSchoolListBean = mutableListOf()
        searchSchoolListAdapter = SearchSchoolListAdapter(this, searchSchoolSchoolListBean)
        rvSchoolLocation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvSchoolLocation.adapter = searchSchoolListAdapter
        rvSchoolLocation.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
        searchSchoolListAdapter.setLoadMoreView(SimpleLoadMoreView())
        // 是否第一次加载出第二页数据
//        searchSchoolListAdapter.bindToRecyclerView(rvSchoolLocation)
//        searchSchoolListAdapter.disableLoadMoreIfNotFullPage()
    }

    private fun initListener() {
        // 取消
        tvSchoolLocationCancel.setOnClickListener { finish() }
        // 输入监听 调用学校列表接口
        etSchoolLocation.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                pageIndex = 1
                mPresenter.getSchoolList(selectCity, pageIndex, pageSize, s.toString(), false)
            }
        })
        // 分页加载
        searchSchoolListAdapter.setOnLoadMoreListener({
            pageIndex++
            mPresenter.getSchoolList(selectCity, pageIndex, pageSize, etSchoolLocation.text.toString(), false)
        }, rvSchoolLocation)
        // 学校列表点击事件
        searchSchoolListAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                selectSchool = (adapter?.data?.get(position) as SchoolListBean).schoolName
                selectSchoolId = (adapter.data[position] as SchoolListBean).schoolId
                val intent = Intent()
                intent.putExtra("selectSchool", selectSchool)
                intent.putExtra("selectSchoolId", selectSchoolId)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("selectSchool", selectSchool)
        intent.putExtra("selectSchoolId", selectSchoolId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // 获取学校列表接口回调
    override fun getSearchSchoolList(dataList: SearchSchoolListBean) {
        if (dataList.list != null) {
            if (pageIndex == 1) {
                searchSchoolSchoolListBean.clear()
                searchSchoolSchoolListBean.addAll(dataList.list)
                searchSchoolListAdapter.setNewData(searchSchoolSchoolListBean)
                searchSchoolListAdapter.setEnableLoadMore(searchSchoolSchoolListBean.size < dataList.totalRowCount)
            } else {
                searchSchoolSchoolListBean.addAll(dataList.list)
                searchSchoolListAdapter.setNewData(searchSchoolSchoolListBean)
                searchSchoolListAdapter.setEnableLoadMore(searchSchoolSchoolListBean.size < dataList.totalRowCount)
                searchSchoolListAdapter.loadMoreComplete()
            }
        }
    }
}