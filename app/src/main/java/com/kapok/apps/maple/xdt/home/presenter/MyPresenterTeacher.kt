package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.MyViewTeacher
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MyPresenterTeacher(context: Context) : BasePresenter<MyViewTeacher>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取用户信息接口
    fun getUserInfo(userid: Int, identitytype: Int, showDialog: Boolean = true) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val userInfoInstance = HomeModelInstance()
            userInfoInstance.getUserInfo(userid, identitytype)
                .execute(object : BaseObserver<UserInfoBean>(mView) {
                    override fun onNext(t: UserInfoBean) {
                        super.onNext(t)
                        mView.getUserInfoBean(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}