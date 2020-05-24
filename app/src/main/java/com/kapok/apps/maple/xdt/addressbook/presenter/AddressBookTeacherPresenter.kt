package com.kapok.apps.maple.xdt.addressbook.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookApplyListBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookDetails
import com.kapok.apps.maple.xdt.addressbook.model.model_instance.AddressBookInstance
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookTeacherView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddressBookTeacherPresenter(context: Context) :
    BasePresenter<AddressBookTeacherView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 查看申请列表
    fun getApplyList(classId: Int, userId: Int) {
        if (checkNetWork()) {
            val checkNoticeModel = AddressBookInstance()
            checkNoticeModel.getApplyList(classId, userId)
                .execute(object : BaseObserver<AddressBookApplyListBean>(mView) {
                    override fun onNext(t: AddressBookApplyListBean) {
                        super.onNext(t)
                        mView.getApplyList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 老师获取班级通讯录列表接口
    fun getAddressBookList(classId: Int,userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val addressBookModel = AddressBookInstance()
            addressBookModel.getAddressBookDetails(classId, userId)
                .execute(object : BaseObserver<AddressBookDetails>(mView) {
                    override fun onNext(t: AddressBookDetails) {
                        super.onNext(t)
                        mView.getAddressDetails(t)
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