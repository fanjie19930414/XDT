package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkAnswerBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherCommentParentBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface StudentAnswerView: BaseView {
    fun getHomeWorkAnswer(bean: HomeWorkAnswerBean)

    fun getHomeWorkComment(list: MutableList<TeacherCommentParentBean>?)

    fun getCommonComment(list: MutableList<CommonComment>?)

    fun createTeacherWorkComment(msg: String)
}