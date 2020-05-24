package com.kapok.apps.maple.xdt.notice.bean

data class NoticeDetailParentBean(
    val classId: Int,
    val className: String,
    val content: String,
    val gmtCreate: String,
    val grade: String,
    val gradeId: Int,
    val images: String,
    val isReceipt: Boolean,
    val isTeacherLeader: Boolean,
    val receipts: MutableList<ReceiveBean>?,
    val receiptId: Int,
    val receiptType: Int,
    val replyStatus: Int,
    val schoolId: Int,
    val schoolName: String,
    val startYear: Int,
    val studentReceiptId: Int?,
    val teacherAvatar: String?,
    val teacherId: Int,
    val teacherName: String,
    val title: String,
    val workId: Int
)