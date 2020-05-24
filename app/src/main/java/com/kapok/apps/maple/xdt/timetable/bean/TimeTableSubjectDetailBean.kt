package com.kapok.apps.maple.xdt.timetable.bean

data class TimeTableSubjectDetailBean(
    val lessonNumber: Int?,
    val lessonType: Int?,
    val subjectId: Int? = -1,
    val subjectName: String?,
    val teacherId: String?,
    val weekDay: Int?
)