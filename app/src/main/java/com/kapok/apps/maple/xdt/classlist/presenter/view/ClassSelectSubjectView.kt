package com.kapok.apps.maple.xdt.classlist.presenter.view

import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface ClassSelectSubjectView : BaseView {
    fun getClassSubjectList(list: MutableList<ClassChooseSubjectBean>?)

    fun editSubjectByTeacher(msg: String)
}