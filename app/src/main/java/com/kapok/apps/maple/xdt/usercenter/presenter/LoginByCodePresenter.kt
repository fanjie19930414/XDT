package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.LoginByCodeBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.LoginByCodeView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LoginByCodePresenter(context: Context) : BasePresenter<LoginByCodeView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 手机验证码登录接口
    fun loginByCode(code: String, phone: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val loginModel = LoginModelInstance()
            loginModel.loginByCode(code, phone)
                .execute(object : BaseObserver<LoginByCodeBean>(mView) {
                    override fun onNext(t: LoginByCodeBean) {
                        super.onNext(t)
                        mView.loginByCodeResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 重新获取验证码
    fun loginMobile(phone: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val loginModel = LoginModelInstance()
            loginModel.loginPhoneCode(phone)
                .execute(object : BaseObserver<Boolean>(mView) {
                    override fun onNext(t: Boolean) {
                        super.onNext(t)
                        mView.getReResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}