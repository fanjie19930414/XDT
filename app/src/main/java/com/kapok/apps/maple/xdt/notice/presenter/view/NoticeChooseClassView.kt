package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kotlin.baselibrary.presenter.view.BaseView

interface NoticeChooseClassView: BaseView {
    fun getTeacherInClasses(bean: MutableList<TeacherInClasses>?)
}