package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.SchoolAndCityListInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SearchCityListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 搜索城市页Presenter
 * fanjie
 */
class SearchCityListPresenter(context: Context) : BasePresenter<SearchCityListView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取城市列表
    fun getCityList(name: String) {
        if (checkNetWork()) {
            // mView.onShowDialog()
            val searchCityList = SchoolAndCityListInstance()
            searchCityList.getCityList(name)
                .execute(object : BaseObserver<MutableList<SearchCityListBean>?>(mView) {
                    override fun onNext(t: MutableList<SearchCityListBean>?) {
                        super.onNext(t)
                        mView.getSearchCityList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}