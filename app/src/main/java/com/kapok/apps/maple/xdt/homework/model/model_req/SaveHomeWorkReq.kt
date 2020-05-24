package com.kapok.apps.maple.xdt.homework.model.model_req

data class SaveHomeWorkReq(
    val content: String,
    val images: String,
    val patriarchId: Int,
    val studentId: Int,
    val workId: Int
)