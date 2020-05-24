package com.kotlin.baselibrary.presenter

import android.content.Context
import com.kotlin.baselibrary.presenter.view.BaseView
import com.kotlin.baselibrary.utils.NetWorkUtils

abstract class BasePresenter<T : BaseView> constructor(private val context : Context) {
    lateinit var mView:T

    /**
     * 接触RxObserver对事件状态的监听 防止内存泄漏
     */
    abstract fun unSubscribe()

    /**
     * 检查网络是否可用
     */
    fun checkNetWork(): Boolean {
        if (NetWorkUtils.isNetWorkAvailable(context)) {
            return true
        }
        mView.onError("网络不可用")
        return false
    }
}