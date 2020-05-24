package com.kapok.apps.maple.xdt.usercenter.model

import com.kapok.apps.maple.xdt.usercenter.bean.*
import io.reactivex.Observable

interface LoginModel {
    // 手机验证码登录(获取)
    fun loginPhoneCode(phone: String): Observable<Boolean>

    // 手机验证码登录
    fun loginByCode(code: String, phone: String): Observable<LoginByCodeBean>

    // 手机号+密码登录接口
    fun loginByPassWord(phone: String, code: String): Observable<LoginByCodeBean>

    // 获取验证码接口找回密码
    fun sendCodeForPWD(phone: String): Observable<Boolean>

    // 找回密码接口
    fun findPWD(code: String, password: String, phone: String): Observable<Boolean>

    // 家长与孩子关系接口
    fun getRelationList(): Observable<MutableList<RelationListBean>?>

    // 学科列表接口
    fun getSubjectList(classId: String, name: String, schoolId: String): Observable<MutableList<SubjectListBean>?>

    // 保存老师信息接口
    fun saveTeacherInfo(name: String, schoolId: Int, sex: String, subjectId: Int, userId: Int): Observable<Boolean>

    // 保存家长信息完善接口
    fun saveParentInfo(name: String, sex: String, userId: Int): Observable<Boolean>

    // 保存孩子信息完善接口
    fun saveChildInfo(relationName: String, name: String, sex: String, userId: Int): Observable<SaveStudentIdBean>

    // 获取年级列表接口
    fun getGradeList(): Observable<MutableList<GradeListBean>?>

    // 创建班级接口
    fun createClass(
        className: String,
        grade: String,
        gradeId: Int,
        schoolId: Int,
        startYear: Int,
        userId: Int
    ): Observable<String>

    // 根据邀请码或手机号搜索班级列表接口
    fun getClassList(code: String, type: Int, userId: Int): Observable<MutableList<JoinClassBean>?>

    // 家长申请加入班级接口
    fun parentApplyJoinClass(classId: Int, userId: Int,studentId: Int): Observable<String>

    // 老师申请加入班级接口
    fun teacherApplyJoinClass(classId: Int, userId: Int): Observable<String>
}