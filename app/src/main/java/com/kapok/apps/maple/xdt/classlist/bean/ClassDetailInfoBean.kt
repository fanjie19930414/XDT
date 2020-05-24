package com.kapok.apps.maple.xdt.classlist.bean

data class ClassDetailInfoBean(
    val avatar: String?,
    val classId: Int,
    val className: String,
    val grade: String,
    val headerTeacher: String,
    val headerTeacherId: Int,
    val headerTeacherPhone: String,
    val parentCount: Int,
    val schoolId: Int,
    val schoolName: String,
    val startYear: Int,
    val state: Int,
    val studentCount: Int,
    val studentId: Int,
    val studentName: String,
    val teacherCount: Int,
    val gradeId: Int
)