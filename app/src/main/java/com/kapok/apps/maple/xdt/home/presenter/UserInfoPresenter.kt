package com.kapok.apps.maple.xdt.home.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.home.bean.IdentityBean
import com.kapok.apps.maple.xdt.home.bean.MyChildrenBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.model.model_instance.HomeModelInstance
import com.kapok.apps.maple.xdt.home.presenter.view.UserInfoView
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_instance.LoginModelInstance
import com.kotlin.baselibrary.commen.BaseOSSBean
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class UserInfoPresenter(context: Context) : BasePresenter<UserInfoView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取用户信息接口
    fun getUserInfo(userid: Int, identitytype: Int, showDialog: Boolean = true) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
            val userInfoInstance = HomeModelInstance()
            userInfoInstance.getUserInfo(userid, identitytype)
                .execute(object : BaseObserver<UserInfoBean>(mView) {
                    override fun onNext(t: UserInfoBean) {
                        super.onNext(t)
                        mView.getUserInfoBean(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取学历接口
    fun getEducationList() {
        if (checkNetWork()) {
            val educationInstance = HomeModelInstance()
            educationInstance.getEducation()
                .execute(object : BaseObserver<MutableList<EducationOrProfessionBean>?>(mView) {
                    override fun onNext(t: MutableList<EducationOrProfessionBean>?) {
                        super.onNext(t)
                        mView.getEducationList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取职业接口
    fun getPerfessionList() {
        if (checkNetWork()) {
            val perfessionInstance = HomeModelInstance()
            perfessionInstance.getPerfession()
                .execute(object : BaseObserver<MutableList<EducationOrProfessionBean>?>(mView) {
                    override fun onNext(t: MutableList<EducationOrProfessionBean>?) {
                        super.onNext(t)
                        mView.getPerfessionList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
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
        userId: Int,
        showDialog: Boolean = false
    ) {
        if (checkNetWork()) {
            if (showDialog) {
                mView.onShowDialog()
            }
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

    // 获取角色列表接口
    fun getIdentityInfo(userid: Int) {
        if (checkNetWork()) {
            val identityInfoInstance = HomeModelInstance()
            identityInfoInstance.getIdentityListBean(userid)
                .execute(object : BaseObserver<MutableList<IdentityBean>>(mView) {
                    override fun onNext(t: MutableList<IdentityBean>) {
                        super.onNext(t)
                        mView.getIdentityInfo(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 切换角色身份接口
    fun changeIdentity(identitytype: Int,userid: Int) {
        if (checkNetWork()) {
            val identityInfoInstance = HomeModelInstance()
            identityInfoInstance.changeIdentity(identitytype,userid)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.changeIdentity(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取上传头像的Token
    fun getOSSToken(objectName: String,type: Int) {
        if (checkNetWork()) {
            val ossTokenInstance = HomeModelInstance()
            ossTokenInstance.getOSSToken(objectName)
                .execute(object : BaseObserver<BaseOSSBean>(mView) {
                    override fun onNext(t: BaseOSSBean) {
                        super.onNext(t)
                        mView.getOSSToken(t,type)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}