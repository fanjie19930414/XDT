package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kotlin.baselibrary.presenter.view.BaseView

interface HomeWorkParentListView: BaseView {
    fun getHomeWorkListParent(bean: HomeWorkListTeacherBean,isFirst: Boolean)
}