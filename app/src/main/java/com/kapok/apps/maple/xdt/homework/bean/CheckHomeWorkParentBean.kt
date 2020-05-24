package com.kapok.apps.maple.xdt.homework.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckHomeWorkParentBean(
    val classId: Int?,
    val className: String?,
    val content: String?,
    val grade: String?,
    val gradeId: Int?,
    val images: String?,
    val schoolId: Int?,
    val schoolName: String?,
    val startYear: Int?,
    val subDate: String?,
    val subUserId: Int?,
    val subUserName: String?,
    val title: String?,
    val workAnswerId: String?
) : Parcelable