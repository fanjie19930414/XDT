package com.kapok.apps.maple.xdt.timetable.model.model_req

import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSubjectDetailBean

data class UpDataTimeTableDetailReq(
    val classId: String,
    val timetableDetailList : MutableList<TimeTableSubjectDetailBean>,
    val weekScope: String
)