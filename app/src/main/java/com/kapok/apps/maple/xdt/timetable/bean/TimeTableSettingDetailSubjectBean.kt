package com.kapok.apps.maple.xdt.timetable.bean

data class TimeTableSettingDetailSubjectBean (
    // 上课时间 8:15
    val lessonBegintime: String,
    // 下课时间 8:15
    val lessonEndtime: String,
    // 第几节课
    val lessonNumber: Int,
    // 课程类型 1：上午；2：下午
    val lessonType: Int
)