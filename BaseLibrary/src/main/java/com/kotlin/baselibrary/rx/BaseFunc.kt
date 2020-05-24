package com.kotlin.baselibrary.rx

import com.kotlin.baselibrary.commen.ResultCode
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * @desciption: 通用数据类型转换封装
 */
class BaseFunc<T> : Function<BaseResponse<T>, Observable<T>> {
    override fun apply(t: BaseResponse<T>): Observable<T> {
        return if (t.code != ResultCode.SUCCESS) {
            Observable.error(BaseException(t.code, t.msg))
        } else {
            if (t.data != null) {
                Observable.just(t.data)
            } else {
                Observable.error(BaseException(t.code, t.msg))
            }
        }
    }
}