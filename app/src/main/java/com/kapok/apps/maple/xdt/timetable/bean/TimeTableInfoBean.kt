package com.kapok.apps.maple.xdt.timetable.bean

data class TimeTableInfoBean(
    val beginDate: String,
    val endDate: String,
    val timeTableDetailList: MutableList<TimeTableDetailListBean>?,
    val weekNumber: Int,
    var totalWeekNumber: Int,
    val currentWeekNumber : Int
)