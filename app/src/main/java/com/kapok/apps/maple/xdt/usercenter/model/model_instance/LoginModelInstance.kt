package com.kapok.apps.maple.xdt.usercenter.model.model_instance

import com.kapok.apps.maple.xdt.usercenter.bean.*
import com.kapok.apps.maple.xdt.usercenter.model.LoginModel
import com.kapok.apps.maple.xdt.usercenter.model.model_req.*
import com.kapok.apps.maple.xdt.usercenter.net.UserLogin
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertBoolean
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class LoginModelInstance : LoginModel {
    // 手机验证码登录(获取验证码)
    override fun loginPhoneCode(phone: String): Observable<Boolean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .loginPhoneCode(LoginPhoneReq(phone))
            .convertBoolean()
    }

    // 手机验证码登录
    override fun loginByCode(code: String, phone: String): Observable<LoginByCodeBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .loginByCode(LoginByCodeReq(phone, code))
            .convert()
    }

    // 账号密码登录
    override fun loginByPassWord(phone: String, code: String): Observable<LoginByCodeBean> {
        // 调用接口 返回结果
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .loginByPassWord(LoginByCodeReq(phone, code))
            .convert()
    }

    // 获取验证码接口找回密码
    override fun sendCodeForPWD(phone: String): Observable<Boolean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .sendCodeForPWD(LoginPhoneReq(phone))
            .convertBoolean()
    }

    // 找回密码接口
    override fun findPWD(code: String, password: String, phone: String): Observable<Boolean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .findPWD(LoginFindPWDReq(code, password, phone))
            .convertBoolean()
    }

    // 家长与孩子关系接口
    override fun getRelationList(): Observable<MutableList<RelationListBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .getRelationList()
            .convert()
    }

    // 学科列表接口
    override fun getSubjectList(
        classId: String,
        name: String,
        schoolId: String
    ): Observable<MutableList<SubjectListBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .getSubjectList(GetSubjectListReq(classId, name, schoolId))
            .convert()
    }

    // 保存老师信息接口
    override fun saveTeacherInfo(
        name: String,
        schoolId: Int,
        sex: String,
        subjectId: Int,
        userId: Int
    ): Observable<Boolean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .saveTeacherInfo(SaveTeacherInfoReq(name, schoolId, sex, subjectId, userId))
            .convertBoolean()
    }

    // 保存家长信息完善接口
    override fun saveParentInfo(name: String, sex: String, userId: Int): Observable<Boolean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .saveParentInfo(SaveParentInfoReq(name, sex, userId))
            .convertBoolean()
    }

    // 保存家长信息完善接口
    override fun saveChildInfo(relationName: String, name: String, sex: String, userId: Int): Observable<SaveStudentIdBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .saveChildInfo(SaveChildInfoReq(relationName, name, sex, userId))
            .convert()
    }

    // 获取年级列表接口
    override fun getGradeList(): Observable<MutableList<GradeListBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .getGradeList()
            .convert()
    }

    // 创建班级接口
    override fun createClass(
        className: String,
        grade: String,
        gradeId: Int,
        schoolId: Int,
        startYear: Int,
        userId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .createClass(CreateClassReq(className, grade, gradeId, schoolId, startYear, userId))
            .convertNoResult()
    }

    // 根据邀请码或手机号搜索班级列表接口
    override fun getClassList(code: String, type: Int, userId: Int): Observable<MutableList<JoinClassBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .getClassList(code, type, userId)
            .convert()
    }

    // 家长申请加入班级接口
    override fun parentApplyJoinClass(classId: Int, userId: Int, studentId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .parentApplyJoinClass(ParentApplyJoinClassReq(classId, userId, studentId))
            .convertNoResult()
    }

    // 老师申请加入班级接口
    override fun teacherApplyJoinClass(classId: Int, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(UserLogin::class.java)
            .teacherApplyJoinClass(TeacherApplyJoinClassReq(classId, userId))
            .convertNoResult()
    }
}