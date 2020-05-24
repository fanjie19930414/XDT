package com.kapok.apps.maple.xdt.addressbook.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookApplyListBean
import com.kapok.apps.maple.xdt.addressbook.model.model_instance.AddressBookInstance
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookNewPersonView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddressBookNewPersonPresenter(context: Context): BasePresenter<AddressBookNewPersonView>(context) {
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

    // 同意申请接口
    fun approvalApply(approvalResult: Int,approvalUserId: Int,classId: Int,userId: Int) {
        if (checkNetWork()) {
            val approvalModel = AddressBookInstance()
            approvalModel.approvalApply(approvalResult, approvalUserId, classId, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.approvalResult(t)
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