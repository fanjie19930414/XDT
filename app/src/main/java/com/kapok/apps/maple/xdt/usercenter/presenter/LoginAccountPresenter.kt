package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.LoginByCodeBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.LoginAccountView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LoginAccountPresenter(context: Context) : BasePresenter<LoginAccountView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 账号密码登录
    fun loginByPassWord(phone: String, code: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val loginModel = LoginModelInstance()
            loginModel.loginByPassWord(phone, code)
                .execute(object : BaseObserver<LoginByCodeBean>(mView) {
                    override fun onNext(t: LoginByCodeBean) {
                        super.onNext(t)
                        t.let {
                            mView.loginResult(it)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}