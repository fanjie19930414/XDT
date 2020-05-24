package com.kotlin.baselibrary.presenter.view

/**
 * P层中持有的View 用于Activity的回调监听
 */
interface BaseView {
    fun onShowDialog()

    fun onDismissDialog()

    fun onError(error : String)
}