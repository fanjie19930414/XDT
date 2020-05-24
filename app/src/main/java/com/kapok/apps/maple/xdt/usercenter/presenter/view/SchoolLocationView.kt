package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface SchoolLocationView : BaseView{
    fun getNearBySchoolList(dataList : MutableList<NearBySchoolBean>?)

    fun getHotSchoolList(dataList : MutableList<NearBySchoolBean>?)
}