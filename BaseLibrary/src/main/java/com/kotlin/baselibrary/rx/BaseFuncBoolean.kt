package com.kotlin.baselibrary.rx

import com.kotlin.baselibrary.commen.ResultCode
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * @desciption: Boolean 类型转换封装
 */
class BaseFuncBoolean<T> : Function<BaseResponse<T>, Observable<Boolean>> {
    override fun apply(t: BaseResponse<T>): Observable<Boolean> {
        return if (t.code != ResultCode.SUCCESS) {
            Observable.error(BaseException(t.code, t.msg))
        } else {
            Observable.just(true)
        }
    }
}