package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SearchSchoolListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface SearchSchoolListView : BaseView {
    fun getSearchSchoolList(dataList: SearchSchoolListBean)
}