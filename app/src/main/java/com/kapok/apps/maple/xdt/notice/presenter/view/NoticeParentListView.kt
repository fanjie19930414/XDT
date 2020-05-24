package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.notice.bean.NoticeListTeacherBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface NoticeParentListView: BaseView {
    fun getNoticeParentListBean(bean: NoticeListTeacherBean,isFirst: Boolean)
}