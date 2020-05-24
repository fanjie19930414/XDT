package com.kapok.apps.maple.xdt.timetable.presenter.view

import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.presenter.view.BaseView

interface TimeTableChooseSubjectView : BaseView {
    fun getClassSubjectList(dataList: MutableList<ClassChooseSubjectBean>?)

    fun getClassTeacherList(dataList: MutableList<TeacherOutPutVOList>?)

    fun saveClassSubject(msg: String)

    fun createNewClass(subjectId: Int)
}