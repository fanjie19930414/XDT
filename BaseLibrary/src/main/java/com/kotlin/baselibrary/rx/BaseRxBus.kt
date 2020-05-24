package com.kotlin.baselibrary.rx

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 事件总线工具类
 */
class BaseRxBus private constructor() {
    private val mBus: Relay<Any> = PublishRelay.create()

    companion object {
        val mBusInstance: BaseRxBus by lazy { BaseRxBus() }
    }

    fun post(event: Any) {
        mBus.accept(event)
    }

    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    fun unSubscribe(disposable: Disposable) {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}