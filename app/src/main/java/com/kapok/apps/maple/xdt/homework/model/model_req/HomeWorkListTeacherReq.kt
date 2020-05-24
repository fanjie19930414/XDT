package com.kapok.apps.maple.xdt.homework.model.model_req

data class HomeWorkListTeacherReq(
    val classId: Int,
    val onlySelfWork: Boolean,
    val pageNo: Int,
    val pageSize: Int,
    val pubEndTime: String,
    val pubStartTime: String,
    val state: String,
    val submitStatus: String,
    val userId: String
)