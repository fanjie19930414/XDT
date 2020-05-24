package com.kapok.apps.maple.xdt.homework.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckHomeWorkTeacherBean(
    val content: String?,
    val classId: Int,
    val className: String,
    val startYear: String,
    val grade: String,
    val gradeId: String,
    val deadline: String,
    val gmtCreate: String,
    val images: String,
    val isTeacherLeader: Boolean,
    val remainTime: String?,
    val state: Int?,
    val subStudentCount: Int?,
    val subjectId: Int,
    val subjectName: String?,
    val teacherAvatar: String?,
    val teacherId: Int?,
    val teacherName: String?,
    val title: String,
    val unSubStudentCount: Int?,
    val workId: Int,
    val workType: Int,
    val workTypeDesc: String?
) : Parcelable