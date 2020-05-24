package com.kapok.apps.maple.xdt.timetable.bean

data class TimeTableSettingInfoBean(
    val amLessonCount: Int,
    val beginDate: String,
    val classId: Int = 4,
    val endDate: String,
    val pmLessonCount: Int,
    val timeTableName: String,
    val timetableConfigDetailList: MutableList<TimeTableSettingDetailSubjectBean>?
)