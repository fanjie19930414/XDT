package com.kapok.apps.maple.xdt.homework.model.model_req

data class CreateCommentReq(
    val content: String,
    val teacherId: Int
)