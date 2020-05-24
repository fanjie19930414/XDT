package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.home.bean.MyChildrenBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface MyViewParent : BaseView {
    // 我的孩子回调
    fun getMyChildren(bean: MutableList<MyChildrenBean>?)

    // 用户信息
    fun getUserInfoBean(bean: UserInfoBean)
}