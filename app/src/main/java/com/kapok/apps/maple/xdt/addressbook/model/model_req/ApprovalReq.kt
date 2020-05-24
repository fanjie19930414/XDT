package com.kapok.apps.maple.xdt.addressbook.model.model_req

data class ApprovalReq (
    val approvalResult: Int,
    val approvalUserId: Int,
    val classId: Int,
    val userId: Int
)