package com.kapok.apps.maple.xdt.classlist.presenter.view

import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.presenter.view.BaseView

interface ClassDetailParentView : BaseView {
    // 班级详情
    fun getClassDetailInfo(bean: ClassDetailInfoBean)

    // 家长作业通知列表接口
    fun getParentNoticeWorkBean(bean: HomeWorkNoticeBean, isFirst: Boolean)
}