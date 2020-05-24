package com.kotlin.baselibrary.rx.event

/**
 * RxBus(自定义传递的类型 用于家长端 班级列表 跳转 详情)
 */
class EventClassListBean<T>(event: T) {
    var data: T? = event
}