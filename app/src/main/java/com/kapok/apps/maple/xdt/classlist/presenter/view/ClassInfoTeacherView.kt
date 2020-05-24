package com.kapok.apps.maple.xdt.classlist.presenter.view

import com.kapok.apps.maple.xdt.classlist.bean.ClassInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface ClassInfoTeacherView : BaseView {
    fun getClassInfoTeacherBean(bean: ClassInfoBean)

    fun exitClass(msg: String)

    fun updateClassDetail(msg: String)
}