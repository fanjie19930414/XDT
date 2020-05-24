package com.kapok.apps.maple.xdt.notice.model.model_req

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeTeacherDetailBean

data class SubmitReceiveReq(
    val receiptId: Int,
    val studentId: Int,
    val userId: Int,
    val workId: Int
)