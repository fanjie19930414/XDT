package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface ChooseGradeView : BaseView {
    fun getGradeList(dataList: MutableList<GradeListBean>?)
}