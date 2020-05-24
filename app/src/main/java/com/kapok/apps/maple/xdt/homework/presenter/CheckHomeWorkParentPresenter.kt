package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.*
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.CheckHomeWorkParentView
import com.kapok.apps.maple.xdt.homework.presenter.view.CheckHomeWorkTeacherView
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkTeacherListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class CheckHomeWorkParentPresenter(context: Context) :
    BasePresenter<CheckHomeWorkParentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 查看作业（家长 已提交的）
    fun checkHomeWorkParentCommited(studentId: Int, teacherId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val checkHomeWorkModel = HomeWorkModelInstance()
            checkHomeWorkModel.checkHomeWorkParentCommit(studentId, teacherId, workId)
                .execute(object : BaseObserver<CheckHomeWorkParentBean?>(mView) {
                    override fun onNext(t: CheckHomeWorkParentBean?) {
                        super.onNext(t)
                        mView.getHomeWorkParentCommit(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 家长查看作业
    fun checkHomeWorkParent(classId: Int, patriarchId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val checkHomeWorkModel = HomeWorkModelInstance()
            checkHomeWorkModel.checkHomeWorkParent(classId, patriarchId, workId)
                .execute(object : BaseObserver<CheckHomeWorkTeacherBean>(mView) {
                    override fun onNext(t: CheckHomeWorkTeacherBean) {
                        super.onNext(t)
                        mView.getHomeWorkParentInfo(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 保存
    fun saveHomeWorkParent(
        content: String,
        images: String,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val saveHomeWorkModel = HomeWorkModelInstance()
            saveHomeWorkModel.saveHomeWorkParent(content, images, patriarchId, studentId, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.saveHomeWorkResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 提交
    fun publishHomeWorkParent(
        content: String,
        images: String,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val publishHomeWorkModel = HomeWorkModelInstance()
            publishHomeWorkModel.publishHomeWorkParent(
                content,
                images,
                patriarchId,
                studentId,
                workId
            ).execute(object : BaseObserver<String>(mView) {
                override fun onNext(t: String) {
                    super.onNext(t)
                    mView.publishHomeWorkResult(t)
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    disposable.add(d)
                }
            })
        }
    }

    // 获取教师点评(家长端)
    fun getTeacherComment(
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val commentHomeWorkModel = HomeWorkModelInstance()
            commentHomeWorkModel.getTeacherComment(patriarchId, studentId, workId)
                .execute(object : BaseObserver<MutableList<TeacherCommentParentBean>?>(mView) {
                    override fun onNext(t: MutableList<TeacherCommentParentBean>?) {
                        super.onNext(t)
                        mView.getTeacherCommentParent(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}