package com.kapok.apps.maple.xdt.classlist.model.model_req

data class ClassUpdateReq(
    val classId: Int,
    val className: String,
    val grade: String,
    val gradeId: Int,
    val userId: Int
)