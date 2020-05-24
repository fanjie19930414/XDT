package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.ChooseGradeView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  选择年级Presenrter
 *  fanjie
 */
class ChooseGradePresenter(context: Context) : BasePresenter<ChooseGradeView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取年级列表
    fun getRelationList() {
        if (checkNetWork()) {
            mView.onShowDialog()
            val gradeModel = LoginModelInstance()
            gradeModel.getGradeList()
                .execute(object : BaseObserver<MutableList<GradeListBean>?>(mView) {
                    override fun onNext(t: MutableList<GradeListBean>?) {
                        super.onNext(t)
                        mView.getGradeList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}