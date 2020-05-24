package com.kotlin.baselibrary.commen

object BaseUserInfo {
    // （0 学生;1 家长;2 老师）身份信息
    var identity: Int = -1
    // 用户Id userId
    var userId: Int = -1
    // 用户名
    var userName: String = ""
    // 用户token
    var token: String = ""
}