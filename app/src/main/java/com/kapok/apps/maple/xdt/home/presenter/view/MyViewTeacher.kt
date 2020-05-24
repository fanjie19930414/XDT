package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface MyViewTeacher : BaseView {
    // 用户信息
    fun getUserInfoBean(bean: UserInfoBean)
}