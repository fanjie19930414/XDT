package com.kapok.apps.maple.xdt.usercenter.net

import android.view.autofill.AutofillId
import com.kapok.apps.maple.xdt.usercenter.bean.*
import com.kapok.apps.maple.xdt.usercenter.model.model_req.*
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.*

// 登录接口
interface UserLogin {
    // 获取验证码接口登陆
    @POST("api/authentication/sendcodeforlogin")
    fun loginPhoneCode(@Body req: LoginPhoneReq): Observable<BaseResponse<Boolean>>

    // 手机验证码登录接口
    @POST("api/authentication/loginbycode")
    fun loginByCode(@Body req: LoginByCodeReq): Observable<BaseResponse<LoginByCodeBean>>

    // 手机号+密码登录接口
    @POST("api/authentication/loginbypassword")
    fun loginByPassWord(@Body req: LoginByCodeReq): Observable<BaseResponse<LoginByCodeBean>>

    // 获取验证码接口找回密码
    @POST("api/authentication/sendcodeforpassword")
    fun sendCodeForPWD(@Body req: LoginPhoneReq): Observable<BaseResponse<Boolean>>

    // 找回密码接口
    @POST("api/authentication/findpassword")
    fun findPWD(@Body req: LoginFindPWDReq): Observable<BaseResponse<Boolean>>

    // 家长与孩子关系接口
    @GET("api/parent/getparentchildrelationlist")
    fun getRelationList(): Observable<BaseResponse<MutableList<RelationListBean>?>>

    // 学科列表接口
    @POST("api/subject/searchsubjectlist")
    fun getSubjectList(@Body req: GetSubjectListReq): Observable<BaseResponse<MutableList<SubjectListBean>?>>

    // 保存老师信息接口
    @POST("api/teacher/updateteacherinfo")
    fun saveTeacherInfo(@Body req: SaveTeacherInfoReq): Observable<BaseResponse<Boolean>>

    // 保存家长信息完善接口
    @POST("api/parent/updateparentinfo")
    fun saveParentInfo(@Body req: SaveParentInfoReq): Observable<BaseResponse<Boolean>>

    // 保存孩子信息完善接口
    @POST("api/parent/createchildinfo")
    fun saveChildInfo(@Body req: SaveChildInfoReq): Observable<BaseResponse<SaveStudentIdBean>>

    // 获取年级列表接口
    @GET("api/class/searchgradelist")
    fun getGradeList(): Observable<BaseResponse<MutableList<GradeListBean>?>>

    // 创建班级接口
    @POST("api/class/createclass")
    fun createClass(@Body req: CreateClassReq): Observable<BaseResponse<String>>

    // 根据邀请码或手机号搜索班级接口
    @GET("api/class/searchclasslistbycode")
    fun getClassList(@Query("code") code: String, @Query("type") type: Int,@Query("userId") userId: Int): Observable<BaseResponse<MutableList<JoinClassBean>?>>

    // 家长申请加入班级接口
    @POST("api/class/createstudentapply")
    fun parentApplyJoinClass(@Body req: ParentApplyJoinClassReq) : Observable<BaseResponse<String>>

    // 老师申请加入班级接口
    @POST("api/class/createteacherapply")
    fun teacherApplyJoinClass(@Body req: TeacherApplyJoinClassReq) : Observable<BaseResponse<String>>
}