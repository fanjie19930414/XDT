package com.kapok.apps.maple.xdt.timetable.bean

data class TimeTableDetailListBean(
    val classId: Int,
    val lessonNumber: Int,
    val lessonType : Int,
    val simpleDate : String,
    val subjectId: Int,
    val subjectName: String,
    val teacherId: Int,
    val teacherMobile: String,
    val teacherName: String?,
    val version: Int,
    val weekDay: Int,
    val weekNumber: Int
)

