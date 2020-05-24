package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.SchoolAndCityListInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SchoolLocationView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 搜索学校页Presenter
 * fanjie
 */
class SchoolLocationPresenter(context: Context) : BasePresenter<SchoolLocationView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取附近学校接口
    fun getSchoolNearBy(city: String, latitude: String, longitude: String, name: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val nearBySchoolInstance = SchoolAndCityListInstance()
            nearBySchoolInstance.getSchoolNearByList(city, latitude, longitude, name)
                .execute(object : BaseObserver<MutableList<NearBySchoolBean>?>(mView) {
                    override fun onNext(t: MutableList<NearBySchoolBean>?) {
                        super.onNext(t)
                        mView.getNearBySchoolList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取热门学校接口
    fun getSchoolHot(city: String, name: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val hotSchoolInstance = SchoolAndCityListInstance()
            hotSchoolInstance.getSchoolHotList(city, name)
                .execute(object : BaseObserver<MutableList<NearBySchoolBean>?>(mView) {
                    override fun onNext(t: MutableList<NearBySchoolBean>?) {
                        super.onNext(t)
                        mView.getHotSchoolList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}