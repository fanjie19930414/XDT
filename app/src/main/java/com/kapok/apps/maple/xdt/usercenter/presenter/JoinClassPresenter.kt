package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.JoinClassBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.JoinClassView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  加入班级类Presenter
 *  fanjie
 */
class JoinClassPresenter(context: Context) : BasePresenter<JoinClassView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取孩子班级列表接口
    fun getClassBean(code: String, type: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val joinClassModel = LoginModelInstance()
            joinClassModel.getClassList(code, type, userId)
                .execute(object : BaseObserver<MutableList<JoinClassBean>?>(mView) {
                    override fun onNext(t: MutableList<JoinClassBean>?) {
                        super.onNext(t)
                        mView.getClassItem(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 家长申请加入班级接口
    fun parentApplayJoinClass(classId: Int, userId: Int, studentId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val parentApplyJoin = LoginModelInstance()
            parentApplyJoin.parentApplyJoinClass(classId, userId, studentId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.parentApplyJoinClass(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 老师申请加入班级接口
    fun teacherApplyJoinClass(classId: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val teacherApplyJoin = LoginModelInstance()
            teacherApplyJoin.teacherApplyJoinClass(classId, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.teacherApplyJoinClass(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}