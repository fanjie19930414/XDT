package com.kapok.apps.maple.xdt.usercenter.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kapok.apps.maple.xdt.usercenter.presenter.view.TeacherEditInfoView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class TeacherEditInfoPresenter(context: Context) : BasePresenter<TeacherEditInfoView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 保存 老师信息完善接口
    fun saveInfo(name: String, schoolId: Int, sex: String, subjectId: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val saveTeacherInfoModel = LoginModelInstance()
            saveTeacherInfoModel.saveTeacherInfo(name, schoolId, sex, subjectId, userId)
                .execute(object : BaseObserver<Boolean>(mView) {
                    override fun onNext(t: Boolean) {
                        super.onNext(t)
                        mView.saveSuccessful(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取学科列表
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
}