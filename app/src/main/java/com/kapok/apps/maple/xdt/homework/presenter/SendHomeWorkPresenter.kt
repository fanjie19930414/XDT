package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.kapok.apps.maple.xdt.homework.model.HomeWorkModel
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.SendHomeWorkView
import com.kapok.apps.maple.xdt.usercenter.bean.GradeListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class SendHomeWorkPresenter(context: Context) : BasePresenter<SendHomeWorkView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取作业类型接口
    fun getHomeWorkType() {
        if (checkNetWork()) {
            val homeWorkTypeModel = HomeWorkModelInstance()
            homeWorkTypeModel.getHomeWorkType()
                .execute(object : BaseObserver<MutableList<EducationOrProfessionBean>?>(mView) {
                    override fun onNext(t: MutableList<EducationOrProfessionBean>?) {
                        super.onNext(t)
                        mView.getWorkType(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取学科列表接口
    fun getSubjectList(classId: String, name: String, schoolId: String) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val subjectModel = LoginModelInstance()
            subjectModel.getSubjectList(classId, name, schoolId)
                .execute(object : BaseObserver<MutableList<SubjectListBean>?>(mView) {
                    override fun onNext(t: MutableList<SubjectListBean>?) {
                        super.onNext(t)
                        mView.getSubjectList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 创建作业接口
    fun createHomeWork(
        content: String,
        deadline: String,
        images: String,
        state: Int,
        studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
        subjectId: Int,
        subjectName: String,
        teacherId: Int,
        title: String,
        videoUri: String,
        workType: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val createHomeWorkModel = HomeWorkModelInstance()
            createHomeWorkModel.createHomeWork(
                content,
                deadline,
                images,
                state,
                studentDetailVoList,
                subjectId,
                subjectName,
                teacherId,
                title,
                videoUri,
                workType
            )
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.createHomeWork(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}