package com.kotlin.baselibrary.net

/**
 * 通用网络接口返回
 */
class BaseResponse<T>(val code: String, val msg: String, val data: T, val succeed: Boolean) {}