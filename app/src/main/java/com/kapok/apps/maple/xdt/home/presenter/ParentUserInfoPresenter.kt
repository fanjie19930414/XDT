package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.ParentUserInfoView
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ParentUserInfoPresenter(context: Context): BasePresenter<ParentUserInfoView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取用户信息接口
    fun getUserInfo(userid: Int, identitytype: Int,showDialog: Boolean = true) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val userInfoInstance = HomeModelInstance()
            userInfoInstance.getUserInfo(userid, identitytype)
                .execute(object : BaseObserver<UserInfoBean>(mView) {
                    override fun onNext(t: UserInfoBean) {
                        super.onNext(t)
                        mView.getUserInfoBean(t)
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

    // 解除家长关系绑定接口
    fun unBindChild(patriarchId: Int, studentId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val unBindChildInstance = HomeModelInstance()
            unBindChildInstance.unbindChild(patriarchId, studentId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.unBindChild(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}