package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkTeacherListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class HomeWorkTeacherListPresenter(context: Context) :
    BasePresenter<HomeWorkTeacherListView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取班级作业列表老师端
    fun getHomeWorkListTeacher(
        classId: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        state: String,
        submitStatus: String,
        teacherId: String,
        isFirst: Boolean
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val homeWorkListModel = HomeWorkModelInstance()
            homeWorkListModel.getHomeWorkListTeacher(
                classId,
                onlySelfWork,
                pageNo,
                pageSize,
                pubEndTime,
                pubStartTime,
                state,
                submitStatus,
                teacherId
            )
                .execute(object : BaseObserver<HomeWorkListTeacherBean>(mView) {
                    override fun onNext(t: HomeWorkListTeacherBean) {
                        super.onNext(t)
                        mView.getHomeWorkListTeacher(t, isFirst)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取老师所在的班级
    fun getTeacherInClasses(teacherId: String) {
        if (checkNetWork()) {
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