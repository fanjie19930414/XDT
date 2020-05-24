package com.kapok.apps.maple.xdt.notice.model.model_req

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeTeacherDetailBean

data class CreateNoticeReq(
    val content: String,
    val images: String,
    val isReceipt: Boolean,
    val publishUserId: Int,
    val receiptContent: MutableList<String>,
    val receiptType: Int,
    val studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
    val teacherDetailVoList: MutableList<NoticeTeacherDetailBean>,
    val title: String
)