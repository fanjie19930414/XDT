package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.DataStatisticsView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DataStatisticsPresenter(context: Context) : BasePresenter<DataStatisticsView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 查看通知接口(回复)
    fun getTeacherNoticeReceiptList(receiptState: Int,teacherId: Int,workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
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

    override fun unSubscribe() {
        disposable.dispose()
    }
}