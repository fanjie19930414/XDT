package com.kapok.apps.maple.xdt.notice.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.notice.bean.NoticeListTeacherBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface NoticeTeacherListView: BaseView {
    fun getNoticeTeacherListBean(bean: NoticeListTeacherBean,isFirst: Boolean)

    fun getTeacherInClasses(list: MutableList<TeacherInClasses>?)
}