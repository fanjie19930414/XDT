package com.kapok.apps.maple.xdt.homework.bean

/**
 * 创建作业时候传入的学生Bean
 */
data class HomeWorkStudentDetailBean (
    val classId: Int,
    val commentStatus: Int = 0, //(点评or提醒状态 0：未点评or提醒 1：已点评or提醒)
    val studentAvatar: String,
    val studentId: Int,
    val studentName: String
)