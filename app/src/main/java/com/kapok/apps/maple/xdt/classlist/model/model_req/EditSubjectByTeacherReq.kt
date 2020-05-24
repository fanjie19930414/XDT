package com.kapok.apps.maple.xdt.classlist.model.model_req

import com.kapok.apps.maple.xdt.classlist.bean.SubjectByTeacherBean

data class EditSubjectByTeacherReq(val classId: Int, val subjectList : MutableList<SubjectByTeacherBean>, val userId: Int)