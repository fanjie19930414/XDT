package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.notice.bean.TeacherInClassesBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface NoticeClassStudentsTeachersView: BaseView {
    fun getStudentInClasses(bean: MutableList<StudentInClasses>?)

    fun getTeacherInClasses(list: MutableList<TeacherInClassesBean>?)
}