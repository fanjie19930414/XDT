package com.kapok.apps.maple.xdt.timetable.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingDetailSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.model.model_instance.TimeTableChooseSubjectInstance
import com.kapok.apps.maple.xdt.timetable.presenter.view.TimeTableSettingView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class TimeTableSettingPresenter(context: Context) : BasePresenter<TimeTableSettingView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取课程表设置接口
    fun getSubjectSetting(classId: Int) {
        if (checkNetWork()) {
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

    // 保存课程表设置接口
    fun saveSubjectSetting(
        amLessonCount: Int,
        beginDate: String,
        classId: Int,
        endDate: String,
        pmLessonCount: Int,
        timeTableName: String,
        timetableConfigDetailList: MutableList<TimeTableSettingDetailSubjectBean>
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val saveClassSettingModel = TimeTableChooseSubjectInstance()
            saveClassSettingModel.saveSubjectSettingInfo(
                amLessonCount,
                beginDate,
                classId,
                endDate,
                pmLessonCount,
                timeTableName,
                timetableConfigDetailList
            )
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.settingResult(t)
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
}