package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.LoginByCodeBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface LoginAccountView : BaseView {
    fun loginResult(bean: LoginByCodeBean)
}