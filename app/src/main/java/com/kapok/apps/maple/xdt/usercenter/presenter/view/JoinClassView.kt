package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.JoinClassBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface JoinClassView : BaseView{
    fun getClassItem(dataList : MutableList<JoinClassBean>?)

    fun parentApplyJoinClass(msg : String)

    fun teacherApplyJoinClass(msg : String)
}