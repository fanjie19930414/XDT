package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkParentBean
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.notice.bean.NoticeDataStatisticsBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailParentBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeDetailTeacherBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.CheckNoticeParentView
import com.kapok.apps.maple.xdt.notice.presenter.view.CheckNoticeTeacherView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class CheckNoticeParentPresenter(context: Context) : BasePresenter<CheckNoticeParentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 查看通知 家长端
    fun checkNoticeParent(
        classId: Int,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val checkNoticeModel = NoticeModelInstance()
            checkNoticeModel.checkNoticeParent(classId,patriarchId, studentId, workId)
                .execute(object : BaseObserver<NoticeDetailParentBean>(mView) {
                    override fun onNext(t: NoticeDetailParentBean) {
                        super.onNext(t)
                        mView.getNoticeDetailParentBean(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 提交回执
    fun submitReceive(
        receiptId: Int,
        studentId: Int,
        userId: Int,
        workId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val submitReceiveModel = NoticeModelInstance()
            submitReceiveModel.submitReceive(receiptId, studentId, userId, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.submitResult(t)
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