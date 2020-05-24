package com.kapok.apps.maple.xdt.classlist.model

import com.kapok.apps.maple.xdt.classlist.bean.*
import com.kapok.apps.maple.xdt.timetable.bean.*
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import io.reactivex.Observable

interface ClassListModel {
    // 班级列表家长端
    fun getClassListParent(userid: Int): Observable<MutableList<ParentClassListBean>?>

    // 班级列表教师端
    fun getClassListTeacher(isleader: Boolean, userid: Int): Observable<MutableList<ParentClassListBean>?>

    // 教师撤回班级申请
    fun cancelClassListTeacher(classId: Int, studentId: Int, userId: Int): Observable<String>

    // 家长撤回班级申请
    fun cancelClassListParent(classId: Int, studentId: Int, userId: Int): Observable<String>

    // 班级详情接口
    fun classDetailInfo(classId: Int, searchType: Int, userId: Int): Observable<ClassDetailInfoBean>

    // 根据班级获取老师列表接口
    fun getClassTeacherList(classId: Int): Observable<MutableList<TeacherOutPutVOList>?>

    // 转移班主任权限接口
    fun changeHeaderTeacher(classId: Int, newTeacherId: Int, oldTeacherId: Int): Observable<String>

    // 解散班级接口
    fun dissolvedClass(classId: Int, searchType: Int, userId: Int): Observable<String>

    // 班级升学接口
    fun classUpdate(classId: Int, className: String, grade: String, gradeId: Int, userId: Int): Observable<String>

    // 获取班级资料教师端接口（代课教师或班主任）
    fun getClassInfoTeacher(classId: Int, searchType: Int, userId: Int): Observable<ClassInfoBean>

    // 退出班级接口
    fun exitClass(classId: Int, quitUserId:Int,searchType: Int, userId: Int): Observable<String>

    // 编辑班级资料接口（班主任）
    fun updateClassInfo(avatar: String, classId: Int, className: String, userId: Int): Observable<String>

    // 获取班级资料家长端接口
    fun getClassInfoParent(classId: Int, searchType: Int, userId: Int): Observable<ClassInfoBean>

    // 获取老师作业通知列表
    fun getTeacherWorkNoticeTeacherList(
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
        userId: Int
    ): Observable<HomeWorkNoticeBean>

    // 获取家长作业通知列表
    fun getParentWorkNoticeParentList(
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
        userId: Int
    ): Observable<HomeWorkNoticeBean>
}