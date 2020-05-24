package com.kapok.apps.maple.xdt.usercenter.model.model_req

/**
 * 创建班级Req
 */
data class CreateClassReq(
    val className: String,
    val grade: String,
    val gradeId: Int,
    val schoolId: Int,
    val startYear: Int,
    val userId: Int
)
