package com.kapok.apps.maple.xdt.timetable.model.model_req

import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingDetailSubjectBean

data class SaveClassSettingReq(
    val amLessonCount: Int,
    val beginDate: String,
    val classId: Int,
    val endDate: String,
    val pmLessonCount: Int,
    val timeTableName: String,
    val timetableDetailList: MutableList<TimeTableSettingDetailSubjectBean>
)