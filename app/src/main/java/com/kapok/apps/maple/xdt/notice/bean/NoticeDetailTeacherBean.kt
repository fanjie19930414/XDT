package com.kapok.apps.maple.xdt.notice.bean

data class NoticeDetailTeacherBean (
    val content: String,
    val gmtCreate: String,
    val images: String,
    val isTeacherLeader: Boolean,
    val noReceiptCount: Int,
    val receiptCount: Int,
    val teacherAvatar: String,
    val teacherId: Int,
    val teacherName: String,
    val title: String,
    val workId: Int,
    val classId: Int,
    val className: String,
    val startYear: String,
    val grade: String,
    val gradeId: Int,
    val schoolId: Int,
    val readCount:Int,
    val noReadCount: Int,
    val isReceipt: Boolean = true
)