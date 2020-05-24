package com.kapok.apps.maple.xdt.timetable.presenter.view

import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface TimeTableTeacherView : BaseView {
    fun getTimeTableSubject(data : TimeTableInfoBean)

    fun getClassSubjectList(dataList: MutableList<ClassChooseSubjectBean>?)

    fun getSettingInfo(data : TimeTableSettingInfoBean)

    fun upDataTimeTableDetail(msg : String)
}