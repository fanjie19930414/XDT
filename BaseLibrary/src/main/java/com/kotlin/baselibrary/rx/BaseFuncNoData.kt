package com.kotlin.baselibrary.rx

import com.kotlin.baselibrary.commen.ResultCode
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * @desciption: 通用数据类型转换封装(返回data为null  需要弹出返回信息)
 */
class BaseFuncNoData<T> : Function<BaseResponse<T>, Observable<String>> {
    override fun apply(t: BaseResponse<T>): Observable<String> {
        return if (t.code != ResultCode.SUCCESS) {
            Observable.error(BaseException(t.code, t.msg))
        } else {
            Observable.just(t.msg)
        }
    }
}