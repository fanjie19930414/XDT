package com.kapok.apps.maple.xdt.home.bean

data class ChildInfoBean(
    val avatar: String?,
    val birthday: String?,
    val classId: Int,
    val className: String,
    val classTeacherId: Int,
    val classTeacherName: String,
    val grade: String,
    val gradeId: Int,
    val headerTeacherPhone: String,
    val patriarchInfos: MutableList<ChildParentBean>?,
    val realName: String,
    val schoolId: Int?,
    val schoolName: String?,
    val startYear: Int,
    val userId: Int
)