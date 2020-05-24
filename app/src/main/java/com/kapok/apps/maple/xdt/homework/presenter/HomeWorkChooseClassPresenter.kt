package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.HomeWorkModel
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkChooseClassView
import com.kapok.apps.maple.xdt.homework.presenter.view.SendHomeWorkView
import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class HomeWorkChooseClassPresenter(context: Context) : BasePresenter<HomeWorkChooseClassView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取老师所在班级
    fun getTeacherInClasses(teacherId: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val gradeModel = HomeWorkModelInstance()
            gradeModel.getTeacherInClasses(teacherId)
                .execute(object : BaseObserver<MutableList<TeacherInClasses>?>(mView) {
                    override fun onNext(t: MutableList<TeacherInClasses>?) {
                        super.onNext(t)
                        mView.getTeacherInClasses(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

}