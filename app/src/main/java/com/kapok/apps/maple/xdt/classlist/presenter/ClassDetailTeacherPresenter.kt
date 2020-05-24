package com.kapok.apps.maple.xdt.classlist.presenter

import android.content.Context
import android.support.v7.view.menu.MenuView
import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.classlist.model.model_instance.ClassListInstance
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassDetailTeacherView
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ClassDetailTeacherPresenter(context: Context) : BasePresenter<ClassDetailTeacherView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取班级详情接口
    fun getClassDetailInfo(classId: Int, searchType: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classDetailInstance = ClassListInstance()
            classDetailInstance.classDetailInfo(classId, searchType, userId)
                .execute(object : BaseObserver<ClassDetailInfoBean>(mView) {
                    override fun onNext(t: ClassDetailInfoBean) {
                        super.onNext(t)
                        mView.getClassDetailInfo(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 根据班级获取老师列表
    fun getClassTeacherList(classId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classTeacherListInstance = ClassListInstance()
            classTeacherListInstance.getClassTeacherList(classId)
                .execute(object : BaseObserver<MutableList<TeacherOutPutVOList>?>(mView) {
                    override fun onNext(t: MutableList<TeacherOutPutVOList>?) {
                        super.onNext(t)
                        mView.getClassTeacherList(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 转移班主任权限接口
    fun changeHeaderTeacher(classId: Int, newTeacherId: Int, oldTeacherId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val changeHeaderTeacherInstance = ClassListInstance()
            changeHeaderTeacherInstance.changeHeaderTeacher(classId, newTeacherId, oldTeacherId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.changeHeaderTeacher(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 解散班级
    fun dissolvedClass(classId: Int, searchType: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val dissolvedClassInstance = ClassListInstance()
            dissolvedClassInstance.dissolvedClass(classId, searchType, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.dissolvedClass(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 班级升学接口
    fun classUpdate(classId: Int, className: String, grade: String, gradeId: Int, userId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val classUpdate = ClassListInstance()
            classUpdate.classUpdate(classId, className, grade, gradeId, userId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.classUpdate(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 获取老师作业通知列表
    fun getTeacherWorkNoticeList(
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
            val teacherWorkNoticeModel = ClassListInstance()
            teacherWorkNoticeModel.getTeacherWorkNoticeTeacherList(
                classId, identityType, onlySelfWork, pageNo, pageSize, pubEndTime, pubStartTime, readStatus, receiptStatus, state, submitStatus, userId
            ).execute(object : BaseObserver<HomeWorkNoticeBean>(mView) {
                override fun onNext(t: HomeWorkNoticeBean) {
                    super.onNext(t)
                    mView.getTeacherNoticeWorkBean(t,isFirst)
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    disposable.add(d)
                }
            })
        }
    }
}