package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.CommitHomeWorkClassInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface CheckHomeWorkTeacherView : BaseView {
    fun getHomeWorkTeacherDetail(bean: CheckHomeWorkTeacherBean)

    fun getCommitHomeWorkListBean(list: MutableList<CommitHomeWorkClassInfoBean>?)

    fun editTimeHomeWorkResult(msg: String)

    fun finishHomeWorkResult(msg: String)

    fun deleteHomeWorkResult(msg: String)

    fun remindHomeWorkResult(msg: String)
}