package com.kapok.apps.maple.xdt.notice.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.notice.bean.NoticeListTeacherBean
import com.kapok.apps.maple.xdt.notice.model.model_instance.NoticeModelInstance
import com.kapok.apps.maple.xdt.notice.presenter.view.NoticeParentListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class NoticeParentListPresenter(context: Context) : BasePresenter<NoticeParentListView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    // 班级通知家长列表
    fun getParentNoticeList(
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
            noticeListModel.getNoticeListParent(
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
                    mView.getNoticeParentListBean(t,isFirst)
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