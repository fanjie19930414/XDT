package com.kapok.apps.maple.xdt.classlist.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.classlist.model.model_instance.ClassListInstance
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassListParentView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ClassListParentPresenter(context: Context) : BasePresenter<ClassListParentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    //  班级列表家长端
    fun getClassListParent(userid: Int,showDialog: Boolean) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val classListInstance = ClassListInstance()
            classListInstance.getClassListParent(userid)
                .execute(object : BaseObserver<MutableList<ParentClassListBean>?>(mView) {
                    override fun onNext(t: MutableList<ParentClassListBean>?) {
                        super.onNext(t)
                        mView.getClassListParent(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 家长撤回班级申请
    fun cancelClassListParent(classId: Int, studentId: Int, userId: Int) {
        if (checkNetWork()) {
            val classListInstance = ClassListInstance()
            classListInstance.cancelClassListParent(classId, studentId, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.cancelClassList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}