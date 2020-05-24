package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.MyChildrenBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.MyViewParent
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MyPresenterParent(context: Context) : BasePresenter<MyViewParent>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取我的孩子接口
    fun getSubjectSetting(userid: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val getMyChildrenInstance = HomeModelInstance()
            getMyChildrenInstance.getMyChildren(userid)
                .execute(object : BaseObserver<MutableList<MyChildrenBean>?>(mView) {
                    override fun onNext(t: MutableList<MyChildrenBean>?) {
                        super.onNext(t)
                        mView.getMyChildren(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
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