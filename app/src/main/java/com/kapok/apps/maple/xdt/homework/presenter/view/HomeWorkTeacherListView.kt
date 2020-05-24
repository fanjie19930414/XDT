package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kotlin.baselibrary.presenter.view.BaseView

interface HomeWorkTeacherListView: BaseView {
    fun getHomeWorkListTeacher(bean: HomeWorkListTeacherBean,isFirst: Boolean)

    fun getTeacherInClasses(list: MutableList<TeacherInClasses>?)
}