package com.kapok.apps.maple.xdt.home.model.model_req

data class UpdateUserInfoReq(
    val avatar: String,
    val birthday: String,
    val education: String,
    val identityType: Int,
    val job: String,
    val realName: String,
    val schoolId: Int,
    val schoolName: String,
    val sex: String,
    val subjectId: Int,
    val subjectName: String,
    val telephone: String,
    val userId: Int
)