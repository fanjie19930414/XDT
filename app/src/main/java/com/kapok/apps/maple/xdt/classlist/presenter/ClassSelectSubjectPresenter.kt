package com.kapok.apps.maple.xdt.classlist.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.classlist.bean.SubjectByTeacherBean
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassSelectSubjectView
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.model.model_instance.TimeTableChooseSubjectInstance
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ClassSelectSubjectPresenter(context: Context) : BasePresenter<ClassSelectSubjectView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取班级科目列表接口
    fun getClassSubjectList(classId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classSubjectModel = TimeTableChooseSubjectInstance()
            classSubjectModel.getClassSubjectList(classId)
                .execute(object : BaseObserver<MutableList<ClassChooseSubjectBean>?>(mView) {
                    override fun onNext(t: MutableList<ClassChooseSubjectBean>?) {
                        super.onNext(t)
                        mView.getClassSubjectList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 编辑老师课程接口（代课教师）
    fun editSubjectByTeacher(classId: Int, subjectList: MutableList<SubjectByTeacherBean>, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val editSubjectByTeacherModel = TimeTableChooseSubjectInstance()
            editSubjectByTeacherModel.editSubjectByTeaceher(classId, subjectList, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.editSubjectByTeacher(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}