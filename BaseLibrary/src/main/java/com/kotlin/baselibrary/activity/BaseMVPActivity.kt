package com.kotlin.baselibrary.activity

import android.os.Bundle
import com.kotlin.baselibrary.custom.ProgressLoading
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.presenter.view.BaseView
import com.kotlin.baselibrary.utils.ToastUtils

/**
 * @desciption: Activity基类，业务相关
 */
open class BaseMVPActivity<T : BasePresenter<*>> : BaseActivity(), BaseView {
    private lateinit var mProgressLoading: ProgressLoading

    lateinit var mPresenter : T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressLoading = ProgressLoading.create(this)
    }

    override fun onShowDialog() {
        mProgressLoading.showLoading()
    }

    override fun onDismissDialog() {
        if (mProgressLoading.isShowing) {
            mProgressLoading.hideLoading()
        }
    }

    override fun onError(error: String) {
        ToastUtils.showMsg(this,error)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.unSubscribe()
    }
}