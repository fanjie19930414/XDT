package com.kapok.apps.maple.xdt.home.bean

data class NewsListBean(
    val content: String,
    val cover: String,
    val gmtCreate: String,
    val gmtModified: String,
    val id: String,
    val link: String,
    val status: Int,
    val title: String
)