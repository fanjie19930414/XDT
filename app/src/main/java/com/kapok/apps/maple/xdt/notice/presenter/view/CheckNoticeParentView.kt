package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailParentBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailTeacherBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface CheckNoticeParentView: BaseView {
    fun getNoticeDetailParentBean(bean: NoticeDetailParentBean)

    fun submitResult(t: String)
}