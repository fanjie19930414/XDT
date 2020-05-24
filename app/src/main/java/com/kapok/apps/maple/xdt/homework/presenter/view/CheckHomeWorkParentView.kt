package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkParentBean
import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherCommentParentBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface CheckHomeWorkParentView : BaseView {
    fun getHomeWorkParentInfo(bean: CheckHomeWorkTeacherBean)

    fun getHomeWorkParentCommit(bean: CheckHomeWorkParentBean?)

    fun saveHomeWorkResult(msg: String)

    fun publishHomeWorkResult(msg: String)

    fun getTeacherCommentParent(list: MutableList<TeacherCommentParentBean>?)
}