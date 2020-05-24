package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.notice.bean.NoticeListTeacherBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.NoticeTeacherListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class NoticeTeacherListPresenter(context: Context) : BasePresenter<NoticeTeacherListView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 获取班级通知列表班主任端
    fun getNoticeListTeacher(
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
            val noticeListModel = NoticeModelInstance()
            noticeListModel.getNoticeListTeacher(
                classId,
                identityType,
                onlySelfWork,
                pageNo,
                pageSize,
                pubEndTime,
                pubStartTime,
                readStatus,
                receiptStatus,
                state,
                submitStatus,
                userId
            ).execute(object : BaseObserver<NoticeListTeacherBean>(mView) {
                override fun onNext(t: NoticeListTeacherBean) {
                    super.onNext(t)
                    mView.getNoticeTeacherListBean(t,isFirst)
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

    override fun unSubscribe() {
        disposable.dispose()
    }
}