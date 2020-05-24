package com.kapok.apps.maple.xdt.homework.model.model_req

data class HomeWorkListParentReq(
    val classId: Int,
    val pageNo: Int,
    val pageSize: Int,
    val userId: String,
    val pubEndTime: String,
    val pubStartTime: String,
    val state: String,
    val submitStatus: String
)