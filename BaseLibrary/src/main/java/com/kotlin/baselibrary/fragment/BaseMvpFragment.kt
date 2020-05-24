package com.kotlin.baselibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.baselibrary.custom.ProgressLoading
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.presenter.view.BaseView
import com.kotlin.baselibrary.utils.ToastUtils

/**
 * @desciption: Fragment基类，业务无关
 */
abstract class BaseMvpFragment<T : BasePresenter<*>> : BaseFragment(), BaseView {

    lateinit var mPresenter: T

    lateinit var mProgressLoading: ProgressLoading

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mProgressLoading = ProgressLoading.create(activity!!)
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
        ToastUtils.showMsg(activity!!,error)
    }
}