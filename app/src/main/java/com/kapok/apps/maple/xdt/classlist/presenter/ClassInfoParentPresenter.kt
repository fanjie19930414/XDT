package com.kapok.apps.maple.xdt.classlist.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.ClassInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.classlist.model.model_instance.ClassListInstance
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassDetailTeacherView
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassInfoParentView
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassInfoTeacherView
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ClassInfoParentPresenter(context: Context) : BasePresenter<ClassInfoParentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 班级资料老师端接口
    fun classUpdate(classId: Int, searchType: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classInfoInstance = ClassListInstance()
            classInfoInstance.getClassInfoParent(classId, searchType, userId)
                .execute(object : BaseObserver<ClassInfoBean>(mView) {
                    override fun onNext(t: ClassInfoBean) {
                        super.onNext(t)
                        mView.getClassInfoTeacherBean(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 退出班级接口
    fun exitClass(classId: Int, quitUserId: Int, searchType: Int, userId: Int) {
        mView.onShowDialog()
        val exitInstance = ClassListInstance()
        exitInstance.exitClass(classId, quitUserId, searchType, userId)
            .execute(object : BaseObserver<String>(mView) {
                override fun onNext(t: String) {
                    super.onNext(t)
                    mView.exitClass(t)
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    disposable.add(d)
                }
            })
    }
}