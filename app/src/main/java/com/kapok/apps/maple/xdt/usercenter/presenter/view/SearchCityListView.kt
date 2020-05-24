package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface SearchCityListView : BaseView {
    fun getSearchCityList(dataList: MutableList<SearchCityListBean>?)
}