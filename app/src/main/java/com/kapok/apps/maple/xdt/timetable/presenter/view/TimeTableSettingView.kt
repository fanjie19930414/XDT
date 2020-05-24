package com.kapok.apps.maple.xdt.timetable.presenter.view

import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface TimeTableSettingView : BaseView {
    fun getClassSubjectList(dataList: MutableList<ClassChooseSubjectBean>?)

    fun settingResult(msg : String)

    fun getSettingInfo(data : TimeTableSettingInfoBean)
}