package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface SplahView: BaseView {
    fun getUserInfoBean(bean: UserInfoBean)
}