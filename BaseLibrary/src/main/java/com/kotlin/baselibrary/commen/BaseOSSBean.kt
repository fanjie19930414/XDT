package com.kotlin.baselibrary.commen

/**
 * OSSToken公共Bean
 */
data class BaseOSSBean(
    val accessId: String,
    val bucket: String,
    val expire: String,
    val fileDir: String,
    val host: String,
    val policy: String,
    val signature: String
)