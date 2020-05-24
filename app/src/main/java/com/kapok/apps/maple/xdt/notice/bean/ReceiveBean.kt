package com.kapok.apps.maple.xdt.notice.bean

data class ReceiveBean (
    var isChoose: Boolean = false,
    val receiptContent: String,
    val receiptId: Int
)