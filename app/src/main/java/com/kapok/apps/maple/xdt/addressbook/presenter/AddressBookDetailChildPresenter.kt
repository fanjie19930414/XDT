package com.kapok.apps.maple.xdt.addressbook.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildDetailBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookDetails
import com.kapok.apps.maple.xdt.addressbook.model.model_instance.AddressBookInstance
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookChildDetailView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddressBookDetailChildPresenter(context: Context) :
    BasePresenter<AddressBookChildDetailView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 获取孩子详情接口
    fun getChildDetail(classId: Int, studentId: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val getChildDetailModel = AddressBookInstance()
            getChildDetailModel.getChildDetail(classId, studentId, userId)
                .execute(object : BaseObserver<AddressBookChildDetailBean>(mView) {
                    override fun onNext(t: AddressBookChildDetailBean) {
                        super.onNext(t)
                        mView.getChildDetail(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 移除班级
    fun detachClass(classId: Int, removeUserId: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val detachClassModel = AddressBookInstance()
            detachClassModel.detachClass(classId, removeUserId, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.detachClass(t)
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