package com.kapok.apps.maple.xdt.timetable.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSubjectDetailBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.model.model_instance.TimeTableChooseSubjectInstance
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableTeacherView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class TimeTableTeacherPresenter(context: Context) : BasePresenter<TimeTableTeacherView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取课程表设置接口
    fun getSubjectSetting(classId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classSettingModel = TimeTableChooseSubjectInstance()
            classSettingModel.getSubjectSettingInfo(classId)
                .execute(object : BaseObserver<TimeTableSettingInfoBean>(mView) {
                    override fun onNext(t: TimeTableSettingInfoBean) {
                        super.onNext(t)
                        mView.getSettingInfo(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取课程表接口
    fun getTimeTableInfo(classid: Int, week: String,showDialog: Boolean) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val getTimeTableModel = TimeTableChooseSubjectInstance()
            getTimeTableModel.getTimeTableInfo(classid, week)
                .execute(object : BaseObserver<TimeTableInfoBean>(mView) {
                    override fun onNext(t: TimeTableInfoBean) {
                        super.onNext(t)
                        mView.getTimeTableSubject(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取班级科目列表接口
    fun getClassSubjectList(classId: Int) {
        if (checkNetWork()) {
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

    // 课程表详情编辑接口
    fun upDateTimeTableDetail(
        classId: String,
        timetableDetailList: MutableList<TimeTableSubjectDetailBean>,
        weekScope: String
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val upDataTimeTableDetailModel = TimeTableChooseSubjectInstance()
            upDataTimeTableDetailModel.upDataTimeTableDetail(classId, timetableDetailList, weekScope)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.upDataTimeTableDetail(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}