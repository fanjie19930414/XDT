package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean
import com.kapok.apps.maple.xdt.notice.bean.NoticeTeacherDetailBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.SendNoticeView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class SendNoticePresenter(context: Context) : BasePresenter<SendNoticeView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 创建通知
    fun createNotice(
        content: String,
        images: String,
        isReceipt: Boolean,
        publishUserId: Int,
        receiptContent: MutableList<String>,
        receiptType: Int,
        studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
        teacherDetailVoList: MutableList<NoticeTeacherDetailBean>,
        title: String
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val createNoticeModel = NoticeModelInstance()
            createNoticeModel.createNotice(content, images, isReceipt, publishUserId, receiptContent, receiptType, studentDetailVoList, teacherDetailVoList, title)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.createNotice(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}