package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.classlist.model.model_instance.ClassListInstance
import com.kapok.apps.maple.xdt.home.bean.ChildInfoBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.ChildrenView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ChildrenInfoPresenter(context: Context) : BasePresenter<ChildrenView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取学生信息
    fun getChildrenInfo(studentId: Int, showDialog: Boolean) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val childrenInstance = HomeModelInstance()
            childrenInstance.getChildInfo(studentId)
                .execute(object : BaseObserver<ChildInfoBean>(mView) {
                    override fun onNext(t: ChildInfoBean) {
                        super.onNext(t)
                        mView.getChildInfo(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 解除家长关系绑定接口
    fun unBindChild(patriarchId: Int, studentId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val unBindChildInstance = HomeModelInstance()
            unBindChildInstance.unbindChild(patriarchId, studentId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.unBindChild(t)
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

    // 更新用户信息接口
    fun updateUserInfo(
        avatar: String,
        birthday: String,
        education: String,
        identityType: Int,
        job: String,
        realName: String,
        schoolId: Int,
        schoolName: String,
        sex: String,
        subjectId: Int,
        subjectName: String,
        telephone: String,
        userId: Int
    ) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val updateUserInfo = HomeModelInstance()
            updateUserInfo.updateUserInfo(
                avatar,
                birthday,
                education,
                identityType,
                job,
                realName,
                schoolId,
                schoolName,
                sex,
                subjectId,
                subjectName,
                telephone,
                userId
            ).execute(object : BaseObserver<String>(mView) {
                override fun onNext(t: String) {
                    super.onNext(t)
                    mView.updateUserInfoResult(t)
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    disposable.add(d)
                }
            })
        }
    }
}