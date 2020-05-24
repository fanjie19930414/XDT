package com.kapok.apps.maple.xdt.home.bean

data class MyChildrenBean(
    val avatar: String?,
    val classId: Int?,
    val className: String?,
    // 0:审核中 1：已通过
    val classState: Int?,
    val leftWorks: Int?,
    val realName: String?,
    val sex: String?,
    val unReadMessage: Int?,
    val studentId: Int?
)