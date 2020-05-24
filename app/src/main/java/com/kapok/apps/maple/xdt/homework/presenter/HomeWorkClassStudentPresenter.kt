package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.HomeWorkModel
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkChooseClassView
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkClassStudentView
import com.kapok.apps.maple.xdt.homework.presenter.view.SendHomeWorkView
import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class HomeWorkClassStudentPresenter(context: Context) : BasePresenter<HomeWorkClassStudentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取老师所在班级
    fun getStudentInClasses(classId: Int,teacherId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val studentModel = HomeWorkModelInstance()
            studentModel.getStudentInClass(classId, teacherId)
                .execute(object : BaseObserver<MutableList<StudentInClasses>?>(mView) {
                    override fun onNext(t: MutableList<StudentInClasses>?) {
                        super.onNext(t)
                        mView.getStudentInClasses(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

}