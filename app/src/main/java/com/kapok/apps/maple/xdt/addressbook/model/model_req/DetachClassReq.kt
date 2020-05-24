package com.kapok.apps.maple.xdt.addressbook.model.model_req

data class DetachClassReq (
    val classId: Int,
    val removeUserId: Int,
    val userId: Int
)