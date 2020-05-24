package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kotlin.baselibrary.presenter.view.BaseView

interface SendNoticeView: BaseView {
    // 创建通知
    fun createNotice(msg: String)
}