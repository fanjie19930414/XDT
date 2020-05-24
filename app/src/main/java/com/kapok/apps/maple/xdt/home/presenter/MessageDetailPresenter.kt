package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.home.bean.IdentityBean
import com.kapok.apps.maple.xdt.home.bean.MyChildrenBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.MessageDetailView
import com.kapok.apps.maple.xdt.home.presenter.view.UserInfoView
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kotlin.baselibrary.commen.BaseOSSBean
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MessageDetailPresenter(context: Context) : BasePresenter<MessageDetailView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }


}