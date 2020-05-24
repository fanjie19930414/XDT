package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SearchSchoolListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.SchoolAndCityListInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SearchCityListView
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SearchSchoolListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 搜索学校页Presenter
 * fanjie
 */
class SearchSchoolListPresenter(context: Context) : BasePresenter<SearchSchoolListView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取城市列表
    fun getSchoolList(
        cityName: String,
        pageIndex: Int,
        pageSize: Int,
        schoolName: String,
        showDialog: Boolean
    ) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val searchSchoolList = SchoolAndCityListInstance()
            searchSchoolList.getSchoolList(cityName, pageIndex, pageSize, schoolName)
                .execute(object : BaseObserver<SearchSchoolListBean>(mView) {
                    override fun onNext(t: SearchSchoolListBean) {
                        super.onNext(t)
                        mView.getSearchSchoolList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}