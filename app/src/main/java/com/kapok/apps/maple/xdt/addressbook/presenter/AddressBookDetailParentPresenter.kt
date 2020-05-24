package com.kapok.apps.maple.xdt.addressbook.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookParentDetailBean
import com.kapok.apps.maple.xdt.addressbook.model.model_instance.AddressBookInstance
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookParentDetailView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddressBookDetailParentPresenter(context: Context) :
    BasePresenter<AddressBookParentDetailView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 获取家长详情接口
    fun getParentDetail(classId: Int, patriarchId: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val getParentDetailModel = AddressBookInstance()
            getParentDetailModel.getParentDetail(classId, patriarchId, userId)
                .execute(object : BaseObserver<AddressBookParentDetailBean>(mView) {
                    override fun onNext(t: AddressBookParentDetailBean) {
                        super.onNext(t)
                        mView.getParentDetail(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    override fun unSubscribe() {
        disposable.dispose()
    }
}