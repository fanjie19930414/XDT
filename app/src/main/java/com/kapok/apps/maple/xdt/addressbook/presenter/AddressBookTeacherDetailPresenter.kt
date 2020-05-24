package com.kapok.apps.maple.xdt.addressbook.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetailBean
import com.kapok.apps.maple.xdt.addressbook.model.model_instance.AddressBookInstance
import com.kapok.apps.maple.xdt.addressbook.presenter.view.AddressBookTeacherDetailView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AddressBookTeacherDetailPresenter(context: Context): BasePresenter<AddressBookTeacherDetailView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

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

    // 移除班级接口
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