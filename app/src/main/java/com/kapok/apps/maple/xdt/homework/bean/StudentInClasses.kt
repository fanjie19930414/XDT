package com.kapok.apps.maple.xdt.homework.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 学生所在班级Bean
 */
@Parcelize
data class StudentInClasses(
    val classId: Int,
    val commentStatus: Int,
    val studentAvatar: String?,
    val studentId: Int,
    val studentName: String,
    var isChoose: Boolean = false
) : Parcelable