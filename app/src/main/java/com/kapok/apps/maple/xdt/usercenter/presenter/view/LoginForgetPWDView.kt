package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kotlin.baselibrary.presenter.view.BaseView

interface LoginForgetPWDView : BaseView {
    fun getCodeResult(isSuccess : Boolean)

    fun changePWEResult(isSuccess : Boolean)
}