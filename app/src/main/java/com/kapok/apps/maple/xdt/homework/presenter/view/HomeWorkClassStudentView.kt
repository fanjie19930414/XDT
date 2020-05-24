package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kotlin.baselibrary.presenter.view.BaseView

interface HomeWorkClassStudentView: BaseView {
    fun getStudentInClasses(bean: MutableList<StudentInClasses>?)
}