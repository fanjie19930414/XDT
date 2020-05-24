package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.classlist.model.model_instance.ClassListInstance
import com.kapok.apps.maple.xdt.home.bean.NewsBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.MessageView
import com.kapok.apps.maple.xdt.home.presenter.view.NewsView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.net.RetrofitFactory
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  消息Presenter
 */
class NewsPresenter(context: Context) : BasePresenter<NewsView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取新闻列表
    fun getNewsList(pageNo: Int, pageSize: Int,isFirst: Boolean) {
        if (checkNetWork()) {
            val newsListInstance = HomeModelInstance()
            newsListInstance.getNewsList(pageNo, pageSize)
                .execute(object : BaseObserver<NewsBean>(mView) {
                    override fun onNext(t: NewsBean) {
                        super.onNext(t)
                        mView.getNewsList(t,isFirst)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}