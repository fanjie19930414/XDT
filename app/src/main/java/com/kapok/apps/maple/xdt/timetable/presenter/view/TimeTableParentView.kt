package com.kapok.apps.maple.xdt.timetable.presenter.view

import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface TimeTableParentView : BaseView {
    fun getTimeTableSubject(data : TimeTableInfoBean)

    fun getSettingInfo(data : TimeTableSettingInfoBean)
}