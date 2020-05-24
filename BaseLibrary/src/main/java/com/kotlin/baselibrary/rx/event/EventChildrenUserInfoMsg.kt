package com.kotlin.baselibrary.rx.event

/**
 * RxBus(自定义传递的类型 用于更新孩子以及用户信息)
 */
class EventChildrenUserInfoMsg<T>(event: T) {
    var data: T? = event
}