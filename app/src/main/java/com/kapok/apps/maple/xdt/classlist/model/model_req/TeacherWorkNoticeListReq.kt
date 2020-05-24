package com.kapok.apps.maple.xdt.classlist.model.model_req

data class TeacherWorkNoticeListReq(
    val classId: Int,
    val identityType: Int, // 0学生 1家长 2老师
    val onlySelfWork: Boolean,
    val pageNo: Int,
    val pageSize: Int,
    val pubEndTime: String,
    val pubStartTime: String,
    val readStatus: Int, // 1未读 2已读
    val receiptStatus: Int,// 1 部分未完成 2 全员已完成
    val state: Int, // 1 未发布 2进行中 3已结束
    val submitStatus: Int,// 1(家长 未提交/部分未完成) 2(家长 已提交/全员已完成)
    val userId: Int
)