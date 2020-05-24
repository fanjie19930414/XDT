package com.kapok.apps.maple.xdt.classlist.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.model.model_instance.ClassListInstance
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassDetailParentView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ClassDetailParentPresenter(context: Context) : BasePresenter<ClassDetailParentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取班级详情接口
    fun getClassDetailInfo(classId: Int, searchType: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classDetailInstance = ClassListInstance()
            classDetailInstance.classDetailInfo(classId, searchType, userId)
                .execute(object : BaseObserver<ClassDetailInfoBean>(mView) {
                    override fun onNext(t: ClassDetailInfoBean) {
                        super.onNext(t)
                        mView.getClassDetailInfo(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取家长作业通知列表(实体Bean类和老师的一致)
    fun getParentWorkNoticeList(
        classId: Int,
        identityType: Int, // 0学生 1家长 2老师
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int, // 1未读 2已读
        receiptStatus: Int,// 1 部分未完成 2 全员已完成
        state: Int, // 1 未发布 2进行中 3已结束
        submitStatus: Int,// 1(家长 未提交/部分未完成) 2(家长 已提交/全员已完成)
        userId: Int,
        isFirst: Boolean
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val parentWorkNoticeModel = ClassListInstance()
            parentWorkNoticeModel.getTeacherWorkNoticeTeacherList(
                classId, identityType, onlySelfWork, pageNo, pageSize, pubEndTime, pubStartTime, readStatus, receiptStatus, state, submitStatus, userId
            ).execute(object : BaseObserver<HomeWorkNoticeBean>(mView) {
                override fun onNext(t: HomeWorkNoticeBean) {
                    super.onNext(t)
                    mView.getParentNoticeWorkBean(t,isFirst)
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    disposable.add(d)
                }
            })
        }
    }
}