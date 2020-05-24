package com.kapok.apps.maple.xdt.notice.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoticeListItemBean(
    val classId: Int?,
    val className: String?,
    val content: String?,
    val deadline: String?,
    val gmtCreate: String,
    val grade: String?,
    val gradeId: Int?,
    val images: String,
    val isTeacherLeader: Boolean,
    val readStatus: Int?,
    val receiptStatus: Int?,
    val remainTime: String?,
    val replayStatus: Int,
    val schoolId: Int?,
    val schoolName: String?,
    val startYear: Int?,
    val subjectId: Int?,
    val subjectName: String?,
    val submitStatus: Int?, // 家长端 1 未提交 2 已提交
    val teacherAvatar: String?,
    val teacherId: Int?,
    val teacherName: String?,
    val title: String?,
    val workId: Int,
    val state: Int?,
    val studentId: Int,
    val workType: Int?,
    val workTypeDesc: String?
) : Parcelable

