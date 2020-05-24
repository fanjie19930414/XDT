package com.kapok.apps.maple.xdt.notice.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 班级下老师Bean
 */
@Parcelize
class TeacherInClassesBean (
    val classId: Int,
    val teacherAvatar: String?,
    val teacherId: Int,
    val teacherName: String,
    var isChoose: Boolean = false
) : Parcelable