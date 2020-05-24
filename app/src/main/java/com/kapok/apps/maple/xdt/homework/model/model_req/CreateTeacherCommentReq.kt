package com.kapok.apps.maple.xdt.homework.model.model_req

data class CreateTeacherCommentReq(
    val content: String,
    val patriarchId: Int,
    val studentId: Int,
    val teacherId: Int,
    val workAnswerId: Int,
    val workId: Int
)