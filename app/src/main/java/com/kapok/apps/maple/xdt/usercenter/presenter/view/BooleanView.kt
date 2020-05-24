package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kotlin.baselibrary.presenter.view.BaseView

/**
 * 返回类型为Boolean的通用View
 */
interface BooleanView : BaseView {
    fun getResult(result: Boolean)
}