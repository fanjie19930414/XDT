package com.kapok.apps.maple.xdt.timetable.model.model_req

import com.kapok.apps.maple.xdt.timetable.bean.SubjectTeacherListBean

data class UpdateClassSubjectInputReq(val classId: Int, val subjectTeacherList: MutableList<SubjectTeacherListBean>,val userId : Int)