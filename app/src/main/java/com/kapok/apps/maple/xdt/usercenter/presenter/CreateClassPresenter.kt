package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.CreateClassView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  创建班级页Presenter
 *  fanjie
 */
class CreateClassPresenter(context: Context) : BasePresenter<CreateClassView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 创建班级接口
    fun createClass(className: String, grade: String, gradeId: Int, schoolId: Int, startYear: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val createClassModel = LoginModelInstance()
            createClassModel.createClass(className, grade, gradeId, schoolId, startYear, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.createResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}