package com.kapok.apps.maple.xdt.classlist.presenter.view

import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.presenter.view.BaseView

interface ClassDetailTeacherView : BaseView {
    // 班级详情
    fun getClassDetailInfo(bean: ClassDetailInfoBean)

    // 老师列表
    fun getClassTeacherList(list: MutableList<TeacherOutPutVOList>?)

    // 转移班主任权限
    fun changeHeaderTeacher(msg: String)

    // 解散班级
    fun dissolvedClass(msg: String)

    // 班级升学
    fun classUpdate(msg: String)

    // 老师作业通知列表接口
    fun getTeacherNoticeWorkBean(bean: HomeWorkNoticeBean,isFirst: Boolean)
}