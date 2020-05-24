package com.kapok.apps.maple.xdt.notice.model.model_req


data class NoticeListTeacherReq(
    val classId: Int,
    val identityType: Int,
    val onlySelfWork: Boolean,
    val pageNo: Int,
    val pageSize: Int,
    val pubEndTime: String,
    val pubStartTime: String,
    val readStatus: Int,
    val receiptStatus: Int,
    val state: Int,
    val submitStatus: Int,
    val userId: Int
)