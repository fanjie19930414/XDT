package com.kapok.apps.maple.xdt.classlist.model.model_instance

import com.kapok.apps.maple.xdt.classlist.bean.ClassDetailInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.ClassInfoBean
import com.kapok.apps.maple.xdt.classlist.bean.HomeWorkNoticeBean
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.classlist.model.ClassListModel
import com.kapok.apps.maple.xdt.classlist.model.model_req.*
import com.kapok.apps.maple.xdt.classlist.net.ClassList
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class ClassListInstance : ClassListModel {
    // 班级列表家长端
    override fun getClassListParent(userid: Int): Observable<MutableList<ParentClassListBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getClassListParent(userid)
            .convert()
    }

    // 班级列表老师端
    override fun getClassListTeacher(isleader: Boolean, userid: Int): Observable<MutableList<ParentClassListBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getClassListTeacher(isleader, userid)
            .convert()
    }

    // 教师撤回班级申请
    override fun cancelClassListTeacher(classId: Int, studentId: Int, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .cancelClassList(CancelClassListTeacherReq(classId, studentId, userId))
            .convertNoResult()
    }

    // 家长撤回班级申请
    override fun cancelClassListParent(classId: Int, studentId: Int, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .cancelClassListParent(CancelClassListTeacherReq(classId, studentId, userId))
            .convertNoResult()
    }

    // 班级详情接口
    override fun classDetailInfo(classId: Int, searchType: Int, userId: Int): Observable<ClassDetailInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getClassDetailInfo(ClassDetailReq(classId, searchType, userId))
            .convert()
    }

    // 根据班级获取老师列表
    override fun getClassTeacherList(classId: Int): Observable<MutableList<TeacherOutPutVOList>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getClassTeacherList(classId)
            .convert()
    }

    // 转移班主任权限接口
    override fun changeHeaderTeacher(classId: Int, newTeacherId: Int, oldTeacherId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .changeHeaderTeacher(ChangeHeaderTeacherReq(classId, newTeacherId, oldTeacherId))
            .convertNoResult()
    }

    // 解散班级接口
    override fun dissolvedClass(classId: Int, searchType: Int, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .dissolvedClass(DissolvedClassReq(classId, searchType, userId))
            .convertNoResult()
    }

    // 班级升学接口
    override fun classUpdate(
        classId: Int,
        className: String,
        grade: String,
        gradeId: Int,
        userId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .classUpdate(ClassUpdateReq(classId, className, grade, gradeId, userId))
            .convertNoResult()
    }

    // 获取班级资料教师端接口（代课教师或班主任）
    override fun getClassInfoTeacher(classId: Int, searchType: Int, userId: Int): Observable<ClassInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getClassInfoTeacher(ClassInfoTeacherReq(classId, searchType, userId))
            .convert()
    }

    // 获取班级资料家长端接口
    override fun getClassInfoParent(classId: Int, searchType: Int, userId: Int): Observable<ClassInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getClassInfoParent(ClassInfoTeacherReq(classId, searchType, userId))
            .convert()
    }

    // 退出班级接口
    override fun exitClass(classId: Int,quitUserId:Int, searchType: Int, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .exitClass(QuiteClassReq(classId, quitUserId,searchType, userId))
            .convertNoResult()
    }

    // 编辑班级资料接口（班主任）
    override fun updateClassInfo(avatar: String, classId: Int, className: String, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .updateClassInfo(UpdateClassInfoReq(avatar, classId, className, userId))
            .convertNoResult()
    }

    // 获取老师作业通知列表
    override fun getTeacherWorkNoticeTeacherList(
        classId: Int,
        identityType: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int,
        receiptStatus: Int,
        state: Int,
        submitStatus: Int,
        userId: Int
    ): Observable<HomeWorkNoticeBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getParentWorkNoticeTeacherList(TeacherWorkNoticeListReq(classId, identityType, onlySelfWork, pageNo, pageSize, pubEndTime, pubStartTime, readStatus, receiptStatus, state, submitStatus, userId))
            .convert()
    }

    // 获取家长作业通知列表
    override fun getParentWorkNoticeParentList(
        classId: Int,
        identityType: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int,
        receiptStatus: Int,
        state: Int,
        submitStatus: Int,
        userId: Int
    ): Observable<HomeWorkNoticeBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(ClassList::class.java)
            .getParentWorkNoticeTeacherList(TeacherWorkNoticeListReq(classId, identityType, onlySelfWork, pageNo, pageSize, pubEndTime, pubStartTime, readStatus, receiptStatus, state, submitStatus, userId))
            .convert()
    }
}