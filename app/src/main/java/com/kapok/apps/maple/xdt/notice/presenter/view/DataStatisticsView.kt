package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface DataStatisticsView: BaseView {
    fun getTeacherDataStatisticsList(list: MutableList<NoticeDataStatisticsBean>?)

    fun remindNotice(msg: String)
}