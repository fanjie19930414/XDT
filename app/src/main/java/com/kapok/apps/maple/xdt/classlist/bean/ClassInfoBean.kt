package com.kapok.apps.maple.xdt.classlist.bean

data class ClassInfoBean(
    val avatar: String?,
    val classId: Int,
    val className: String,
    val grade: String,
    val gradeId: Int,
    val headerTeacher: String,
    val headerTeacherId: Int,
    val headerTeacherPhone: String,
    val parentCount: Int,
    val schoolId: Int,
    val schoolName: String,
    val searchSubjectOutputVOS: List<ClassInfoSubjectBean>?,
    val startYear: Int,
    val state: Int,
    val studentCount: Int,
    val studentId: Int,
    val studentName: String,
    val teacherCount: Int
)