package com.kotlin.baselibrary.rx.event

/**
 * RxBus(自定义传递的类型 用于用户切换身份 跳转不同的fragment)
 */
class EventChangeUserIdentity<T>(event: T) {
    var data: T? = event
}