package com.kapok.apps.maple.xdt.classlist.bean

import java.io.Serializable

data class ParentClassListBean(
    // 班级头像
    val avatar: String?,
    // 班级编号
    val classId: Int,
    // 班级名称
    val className: String?,
    // 年级
    val grade: String?,
    // 班主任名称
    val headerTeacher: String?,
    // 班主任id
    val headerTeacherId: Int?,
    // 班主任电话
    val headerTeacherPhone: String,
    // 家长数量
    val parentCount: Int?,
    // 学校id
    val schoolId: Int?,
    // 所属学校
    val schoolName: String?,
    // 入学年份
    val startYear: Int?,
    // 0:审核中（待审核） 1：已通过(已加入)
    val state: Int?,
    // 学生数量
    val studentCount: Int?,
    // 学生ID
    val studentId: Int,
    // 学生姓名
    val studentName: String?,
    // 老师数量
    val teacherCount: Int?
) : Serializable