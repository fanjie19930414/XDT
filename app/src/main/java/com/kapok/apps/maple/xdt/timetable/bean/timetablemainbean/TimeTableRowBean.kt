package com.kapok.apps.maple.xdt.timetable.bean.timetablemainbean

data class TimeTableRowBean(
    var lessonNumber: Int,
    var lessonType: Int,
    var subjectId: Int = -1,
    var subjectName: String = "",
    var teacherId: String = "",
    var weekDay: Int,
    var teacherName : String? = ""
)