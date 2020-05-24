package com.kotlin.baselibrary.ex

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.net.BaseResponse
import com.kotlin.baselibrary.rx.BaseFunc
import com.kotlin.baselibrary.rx.BaseFuncBoolean
import com.kotlin.baselibrary.rx.BaseFuncNoData
import com.kotlin.baselibrary.rx.BaseObserver
import com.kotlin.baselibrary.utils.GlideUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 通用数据转换
 */
fun <T> Observable<T>.execute(baseObserver: BaseObserver<T>) {
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(baseObserver)
}

/**
 * 扩展数据转换(当返回data为null 并且需要弹出返回的msg)
 */
fun <T> Observable<BaseResponse<T>>.convertNoResult(): Observable<String> {
    return this.flatMap(BaseFuncNoData())
}

/**
 * 扩展数据转换
 */
fun <T> Observable<BaseResponse<T>>.convert(): Observable<T> {
    return this.flatMap(BaseFunc())
}

/**
 * 扩展Boolean类型数据转换
 */
fun <T> Observable<BaseResponse<T>>.convertBoolean(): Observable<Boolean> {
    return this.flatMap(BaseFuncBoolean())
}

/**
 * 扩展点击事件
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

/**
 * 扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}

/**
 * 扩展Button可用性
 */
fun Button.enable(et: EditText, method: () -> Boolean) {
    val btn = this
    et.addTextChangedListener(object : DefaultTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            btn.isEnabled = method()
        }
    })
}

/**
 * ImageView加载网络图片
 */
fun ImageView.loadUrl(url: String) {
    GlideUtils.loadUrlImage(context, url, this)
}

/*
扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}