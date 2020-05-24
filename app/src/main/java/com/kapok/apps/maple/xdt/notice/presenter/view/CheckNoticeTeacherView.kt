package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailTeacherBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface CheckNoticeTeacherView: BaseView {
    fun getNoticeDetailTeacherBean(bean: NoticeDetailTeacherBean)

    fun getRePubResult(msg: String)

    fun remindNotice(msg: String)

    fun deleteNotice(msg: String)

    fun getTeacherDataStatisticsList(t: MutableList<NoticeDataStatisticsBean>?)
}