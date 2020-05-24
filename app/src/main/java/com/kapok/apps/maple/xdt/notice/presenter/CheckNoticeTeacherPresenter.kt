package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkParentBean
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailTeacherBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.CheckNoticeTeacherView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class CheckNoticeTeacherPresenter(context: Context): BasePresenter<CheckNoticeTeacherView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 查看通知 班主任
    fun checkNoticeTeacher(classId: Int,teacherId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val checkNoticeModel = NoticeModelInstance()
            checkNoticeModel.checkNoticeTeacher(classId,teacherId,workId)
                .execute(object : BaseObserver<NoticeDetailTeacherBean>(mView){
                    override fun onNext(t: NoticeDetailTeacherBean) {
                        super.onNext(t)
                        mView.getNoticeDetailTeacherBean(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 查看通知接口(回复)  用于知道哪些学生需要提醒
    fun getTeacherNoticeReceiptList(receiptState: Int,teacherId: Int,workId: Int) {
        if (checkNetWork()) {
            val noticeReceiptModel = NoticeModelInstance()
            noticeReceiptModel.getTeacherNoticeReceiptList(receiptState, teacherId, workId)
                .execute(object : BaseObserver<MutableList<NoticeDataStatisticsBean>?>(mView) {
                    override fun onNext(t: MutableList<NoticeDataStatisticsBean>?) {
                        super.onNext(t)
                        mView.getTeacherDataStatisticsList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 再次发布
    fun publishNoticeAgain(teacherId: Int,workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val publishNoticeModel = NoticeModelInstance()
            publishNoticeModel.publishNoticeAgain(teacherId, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.getRePubResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 一键提醒
    fun remindNotice(studentIds: MutableList<Int>, teacherId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val remindNoticeModel = NoticeModelInstance()
            remindNoticeModel.remindNotice(studentIds, teacherId, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.remindNotice(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 删除通知
    fun delectNotice(teacherId: Int,workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val deleteNoticeModel = NoticeModelInstance()
            deleteNoticeModel.delectNotice(teacherId, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.deleteNotice(t)
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