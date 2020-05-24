package com.kapok.apps.maple.xdt.addressbook.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildDetailBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetailBean
import com.kapok.apps.maple.xdt.addressbook.model.model_instance.AddressBookInstance
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookHandleDetailView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddressBookHandleDetailPresenter(context: Context):
    BasePresenter<AddressBookHandleDetailView>(context) {
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

    // 获取老师详情接口
    fun getTeacherDetail(classId: Int,teacherId: Int,userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val teacherDetailModel = AddressBookInstance()
            teacherDetailModel.getTeacherDetail(classId, teacherId, userId)
                .execute(object : BaseObserver<AddressBookTeacherDetailBean>(mView) {
                    override fun onNext(t: AddressBookTeacherDetailBean) {
                        super.onNext(t)
                        mView.getTeacherDetail(t)
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