package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.LoginForgetPWDView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  登录页 (忘记密码Presenter)
 *  fanjie
 */
class LoginForgetPWDPresenter(context: Context) : BasePresenter<LoginForgetPWDView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取验证码接口找回密码
    fun sendCodeForPWD(phone: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val loginModel = LoginModelInstance()
            loginModel.sendCodeForPWD(phone)
                .execute(object : BaseObserver<Boolean>(mView) {
                    override fun onNext(t: Boolean) {
                        super.onNext(t)
                        mView.getCodeResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 找回密码接口
    fun findPWD(code: String, password: String, phone: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val loginModel = LoginModelInstance()
            loginModel.findPWD(code, password, phone)
                .execute(object : BaseObserver<Boolean>(mView) {
                    override fun onNext(t: Boolean) {
                        super.onNext(t)
                        mView.changePWEResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}