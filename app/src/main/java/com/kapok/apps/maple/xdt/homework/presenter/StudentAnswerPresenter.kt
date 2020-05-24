package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkAnswerBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherCommentParentBean
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.StudentAnswerView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class StudentAnswerPresenter(context: Context) : BasePresenter<StudentAnswerView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 查看作业作答
    fun getWorkAnswer(classId: Int, studentId: Int, workId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val getWorkAnswerModel = HomeWorkModelInstance()
            getWorkAnswerModel.getWorkAnswer(classId, studentId, workId)
                .execute(object : BaseObserver<HomeWorkAnswerBean>(mView) {
                    override fun onNext(t: HomeWorkAnswerBean) {
                        super.onNext(t)
                        mView.getHomeWorkAnswer(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取教师评论
    fun getHomeWorkComment(studentId: Int, teacherId: Int, workId: Int) {
        if (checkNetWork()) {
            val getCommentModel = HomeWorkModelInstance()
            getCommentModel.getHomeWorkComment(studentId, teacherId, workId)
                .execute(object : BaseObserver<MutableList<TeacherCommentParentBean>?>(mView) {
                    override fun onNext(t: MutableList<TeacherCommentParentBean>?) {
                        super.onNext(t)
                        mView.getHomeWorkComment(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取常用评语
    fun getCommonComment(teacherId: Int) {
        if (checkNetWork()) {
            val getCommonCommentModel = HomeWorkModelInstance()
            getCommonCommentModel.getCommonComment(teacherId)
                .execute(object : BaseObserver<MutableList<CommonComment>?>(mView) {
                    override fun onNext(t: MutableList<CommonComment>?) {
                        super.onNext(t)
                        mView.getCommonComment(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 提交教师评语
    fun createTeacherWorkComment(
        content: String,
        patriarchId: Int,
        studentId: Int,
        teacherId: Int,
        workAnswerId: Int,
        workId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val createTeacherModel = HomeWorkModelInstance()
            createTeacherModel.createTeacherComment(content, patriarchId, studentId, teacherId, workAnswerId, workId)
                .execute(object : BaseObserver<String>(mView){
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.createTeacherWorkComment(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}