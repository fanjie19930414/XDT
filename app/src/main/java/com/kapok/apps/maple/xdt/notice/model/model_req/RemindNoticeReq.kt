package com.kapok.apps.maple.xdt.notice.model.model_req

data class RemindNoticeReq (
    val studentIds: MutableList<Int>,
    val teacherId: Int,
    val workId: Int
)