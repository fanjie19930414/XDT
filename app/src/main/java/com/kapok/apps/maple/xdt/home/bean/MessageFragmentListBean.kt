package com.kapok.apps.maple.xdt.home.bean

/**
     消息一级列表实体类
 */
data class MessageFragmentListBean(
    // 类型 1通知 2作业 3课程表 4动态
    val deliveryMode: Int,
    val messageContent: String?,
    val messageTime: String?,
    val messageTitle: String?,
    val messageBrief: String?,
    var unReadMessageCount: Int
)