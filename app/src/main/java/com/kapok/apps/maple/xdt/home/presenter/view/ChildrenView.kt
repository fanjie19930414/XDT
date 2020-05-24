package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.home.bean.ChildInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface ChildrenView : BaseView {
    fun getChildInfo(bean: ChildInfoBean)

    fun exitClass(msg: String)

    fun unBindChild(msg: String)

    fun updateUserInfoResult(msg: String)
}