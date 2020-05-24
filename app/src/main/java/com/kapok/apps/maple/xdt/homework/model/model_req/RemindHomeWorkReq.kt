package com.kapok.apps.maple.xdt.homework.model.model_req

data class RemindHomeWorkReq(
    val studentIds: MutableList<Int>,
    val teacherId: Int,
    val workId: Int
)