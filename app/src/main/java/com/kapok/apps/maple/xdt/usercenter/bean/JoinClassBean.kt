package com.kapok.apps.maple.xdt.usercenter.bean


data class JoinClassBean(
    // 班级头像
    val avatar: String?,
    // 班级编号
    val classId: Int,
    // 班级名称
    val className: String,
    // 年级
    val grade: String,
    // 班主任名称
    val headerTeacher: String,
    // 所属学校
    val schoolName: String,
    //学年
    val startYear: Int
)