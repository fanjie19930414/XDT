package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.LoginByCodeBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface LoginByCodeView : BaseView {
    fun loginByCodeResult(bean : LoginByCodeBean)

    fun getReResult(isSuccess : Boolean)
}