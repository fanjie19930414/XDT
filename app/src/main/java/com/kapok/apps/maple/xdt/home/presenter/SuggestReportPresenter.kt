package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.SuggestReportView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class SuggestReportPresenter(context: Context) : BasePresenter<SuggestReportView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 提交意见反馈接口
    fun reportSuggest(content: String, userId: Int,showDialog: Boolean) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val userInfoInstance = HomeModelInstance()
            userInfoInstance.reportSuggest(content, userId)
                .execute(object : BaseObserver<String>(mView){
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.reportSuggest(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}