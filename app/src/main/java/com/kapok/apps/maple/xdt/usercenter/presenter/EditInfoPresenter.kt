package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SaveStudentIdBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.SchoolAndCityListInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.EditInfoView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseException
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class EditInfoPresenter(context: Context) : BasePresenter<EditInfoView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 家长信息完善接口(先调 家长信息接口  再调 孩子信息完善接口)
    fun saveParentInfo(name: String, sex: String, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val saveParentModel = LoginModelInstance()
            saveParentModel.saveParentInfo(name, sex, userId)
                .execute(object : BaseObserver<Boolean>(mView) {
                    override fun onNext(t: Boolean) {
                        mView.saveParentSuccessful(t)
                    }

                    override fun onError(e: Throwable) {
                        if (e is BaseException) {
                            e.msg.let { mView.onError(it) }
                        } else {
                            e.message?.let { mView.onError(it) }
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 孩子信息完善接口(先调 家长信息接口  再调 孩子信息完善接口)
    fun saveChildInfo(relationName: String, name: String, sex: String, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val saveChildModel = LoginModelInstance()
            saveChildModel.saveChildInfo(relationName, name, sex, userId)
                .execute(object : BaseObserver<SaveStudentIdBean>(mView) {
                    override fun onNext(t: SaveStudentIdBean) {
                        super.onNext(t)
                        mView.saveChildSuccessful(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 家长与孩子关系接口复制
    fun getRelationList() {
        if (checkNetWork()) {
            mView.onShowDialog()
            val relationModel = LoginModelInstance()
            relationModel.getRelationList()
                .execute(object : BaseObserver<MutableList<RelationListBean>?>(mView) {
                    override fun onNext(t: MutableList<RelationListBean>?) {
                        super.onNext(t)
                        mView.getRelationList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}