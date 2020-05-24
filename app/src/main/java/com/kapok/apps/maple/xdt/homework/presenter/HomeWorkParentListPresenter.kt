package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkParentListView
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkTeacherListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class HomeWorkParentListPresenter(context: Context) :
    BasePresenter<HomeWorkParentListView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取班级作业列表家长端
    fun getHomeWorkListParent(
        classId: Int,
        pageNo: Int,
        pageSize: Int,
        patriachId: String,
        pubEndTime: String,
        pubStartTime: String,
        state: String,
        submitStatus: String,
        isFirst: Boolean
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val homeWorkListParentModel = HomeWorkModelInstance()
            homeWorkListParentModel.getHomeWorkListParent(
                classId,
                pageNo,
                pageSize,
                patriachId,
                pubEndTime,
                pubStartTime,
                state,
                submitStatus
            )
                .execute(object : BaseObserver<HomeWorkListTeacherBean>(mView) {
                    override fun onNext(t: HomeWorkListTeacherBean) {
                        super.onNext(t)
                        mView.getHomeWorkListParent(t, isFirst)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}