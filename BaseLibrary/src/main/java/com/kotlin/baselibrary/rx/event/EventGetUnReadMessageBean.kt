package com.kotlin.baselibrary.rx.event

/**
 * RxBus(自定义Message接收未读消息数量)
 */
class EventGetUnReadMessageBean<T>(event: T,mode: Int,isNotify: Boolean) {
    var data: T? = event
    var type: Int = mode
    var notify: Boolean = isNotify
}