package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.adapter.SearchCityListAdapter
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kapok.apps.maple.xdt.usercenter.presenter.SearchCityListPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SearchCityListView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.utils.Dp2pxUtils
import kotlinx.android.synthetic.main.activity_search_city_list.*

/**
 * 搜索城市列表页
 * fanjie
 */
class SearchCityListActivity : BaseMVPActivity<SearchCityListPresenter>(), SearchCityListView {
    // 城市列表集合
    lateinit var cityList: MutableList<SearchCityListBean>
    lateinit var cityListAdapter: SearchCityListAdapter
    // 选择的城市
    private var selectCity : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_city_list)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = SearchCityListPresenter(this)
        mPresenter.mView = this
        // 选中的城市
        val intent = intent
        selectCity = intent.getStringExtra("cityName")
        // 配置RecyclerView
        cityList = mutableListOf()
        cityListAdapter = SearchCityListAdapter(this, cityList)
        rvCityLocation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCityLocation.adapter = cityListAdapter
        rvCityLocation.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
    }

    private fun initListener() {
        // 取消
        tvCityLocationCancel.setOnClickListener { finish() }
        // 输入监听 调用城市列表接口
        etCityLocation.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                mPresenter.getCityList(s.toString().trim())
            }
        })
        // 点击城市列表 返回给搜索学校页
        cityListAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val selectCityName = cityList[position].cityName
                val selectCityId = cityList[position].cityId
                val intent = Intent()
                intent.putExtra("selectCityName", selectCityName)
                intent.putExtra("selectCityId", selectCityId)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("selectCityName", selectCity)
        intent.putExtra("selectCityId", -1)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // 返回城市列表回调
    override fun getSearchCityList(dataList: MutableList<SearchCityListBean>?) {
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                cityList.clear()
                cityList.addAll(dataList)
                cityListAdapter.notifyDataSetChanged()
            }
        }
    }
}