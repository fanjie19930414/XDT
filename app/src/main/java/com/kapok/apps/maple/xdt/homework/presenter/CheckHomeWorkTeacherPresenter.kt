package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.CheckHomeWorkTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.CommitHomeWorkClassInfoBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListTeacherBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.CheckHomeWorkTeacherView
import com.kapok.apps.maple.xdt.homework.presenter.view.HomeWorkTeacherListView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class CheckHomeWorkTeacherPresenter(context: Context) :
    BasePresenter<CheckHomeWorkTeacherView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 查看作业（老师）
    fun checkHomeWorkTeacher(teacherId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val checkHomeWorkModel = HomeWorkModelInstance()
            checkHomeWorkModel.checkHomeWorkTeacher(teacherId, workId)
                .execute(object : BaseObserver<CheckHomeWorkTeacherBean>(mView) {
                    override fun onNext(t: CheckHomeWorkTeacherBean) {
                        super.onNext(t)
                        mView.getHomeWorkTeacherDetail(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 老师获取班级提交作业列表
    fun getCommitHomeWorkList(submitState: Int, teacherId: Int, workId: Int, showDialog: Boolean) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val commitHomeWorkListModel = HomeWorkModelInstance()
            commitHomeWorkListModel.getCommitHomeWorkListTeacher(submitState, teacherId, workId)
                .execute(object : BaseObserver<MutableList<CommitHomeWorkClassInfoBean>?>(mView) {
                    override fun onNext(t: MutableList<CommitHomeWorkClassInfoBean>?) {
                        super.onNext(t)
                        mView.getCommitHomeWorkListBean(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 修改截至日期
    fun editHomeWorkTime(deadline: String, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val editTimeHomeWorkModel = HomeWorkModelInstance()
            editTimeHomeWorkModel.editHomeWorkTeacher(deadline, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.editTimeHomeWorkResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 立即结束
    fun finishHomeWork(workId: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val finishHomeWorkModel = HomeWorkModelInstance()
            finishHomeWorkModel.finishHomeWorkTeacher(workId.toInt())
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.finishHomeWorkResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 删除作业
    fun deleteHomeWork(teacherId: String, workId: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val deleteHomeWorkModel = HomeWorkModelInstance()
            deleteHomeWorkModel.deleteHomeWorkTeacher(teacherId.toInt(), workId.toInt())
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.deleteHomeWorkResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 作业一键提醒
    fun remindHomeWork(studentIds: MutableList<Int>, teacherId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val remindHomeWorkModel = HomeWorkModelInstance()
            remindHomeWorkModel.remindHomeWorkTeacher(studentIds, teacherId, workId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.remindHomeWorkResult(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}