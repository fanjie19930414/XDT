package com.kapok.apps.maple.xdt.timetable.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kapok.apps.maple.xdt.timetable.model.model_instance.TimeTableChooseSubjectInstance
import com.kapok.apps.maple.xdt.timetable.bean.SubjectTeacherListBean
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableChooseSubjectView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 课程表 选择课程页面Presenter
 * fanjie
 */
class TimeTableChooseSubjectPresenter(context: Context) : BasePresenter<TimeTableChooseSubjectView>(context) {
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

    // 根据班级获取老师列表接口
    fun getClassTeacherList(classId: Int) {
        if (checkNetWork()) {
            val classTeacherModel = TimeTableChooseSubjectInstance()
            classTeacherModel.getClassTeacherList(classId)
                .execute(object : BaseObserver<MutableList<TeacherOutPutVOList>?>(mView) {
                    override fun onNext(t: MutableList<TeacherOutPutVOList>?) {
                        super.onNext(t)
                        mView.getClassTeacherList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 班级科目编辑接口
    fun saveClassSubject(classId: Int, subjectTeacherListBean: MutableList<SubjectTeacherListBean>,userId : Int) {
        if (checkNetWork()) {
            val saveClassSubjectModel = TimeTableChooseSubjectInstance()
            saveClassSubjectModel.saveClassSubject(classId, subjectTeacherListBean,userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.saveClassSubject(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 创建新科目接口
    fun createNewSubject(classId: Int, subjectName: String) {
        if (checkNetWork()) {
            val createNewSubjectModel = TimeTableChooseSubjectInstance()
            createNewSubjectModel.createNewSubject(classId, subjectName)
                .execute(object : BaseObserver<Int>(mView) {
                    override fun onNext(t: Int) {
                        super.onNext(t)
                        mView.createNewClass(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}