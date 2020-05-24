package com.kotlin.baselibrary.rx

import com.kotlin.baselibrary.presenter.view.BaseView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


open class BaseObserver<T>(private val baseView: BaseView) : Observer<T> {
    override fun onComplete() {
        baseView.onDismissDialog()
    }

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: T) {
        baseView.onDismissDialog()
    }

    override fun onError(e: Throwable) {
        baseView.onDismissDialog()
        if (e is BaseException) {
            e.msg.let { baseView.onError(it)}
        } else {
            e.message?.let { baseView.onError(it) }
        }
    }


}