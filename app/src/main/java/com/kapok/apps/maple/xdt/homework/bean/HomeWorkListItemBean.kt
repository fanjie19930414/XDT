package com.kapok.apps.maple.xdt.homework.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeWorkListItemBean(
    val content: String,
    val deadline: String,
    val gmtCreate: String,
    val images: String,
    val isTeacherLeader: Boolean,
    val remainTime: String?,
    val subjectId: Int,
    val subjectName: String,
    val teacherAvatar: String?,
    val teacherId: Int,
    val teacherName: String?,
    val title: String,
    val workId: Int,
    val state: Int,
    val submitStatus: Int?, // 家长端 1 未提交 2 已提交
    val replyStatus: Int?,// 家长端 1 未回复 2 已回复
    val studentId: Int = 0
) : Parcelable

