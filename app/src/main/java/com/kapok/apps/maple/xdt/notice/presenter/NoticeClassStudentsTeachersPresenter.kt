package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.StudentInClasses
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.notice.bean.TeacherInClassesBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.NoticeClassStudentsTeachersView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class NoticeClassStudentsTeachersPresenter(context: Context) :
    BasePresenter<NoticeClassStudentsTeachersView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取班级学生
    fun getStudentInClasses(classId: Int, teacherId: Int) {
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

    // 获取班级老师
    fun getTeacherInClasses(classId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val teacherModel = NoticeModelInstance()
            teacherModel.getTeacherInClasses(classId)
                .execute(object : BaseObserver<MutableList<TeacherInClassesBean>?>(mView) {
                    override fun onNext(t: MutableList<TeacherInClassesBean>?) {
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