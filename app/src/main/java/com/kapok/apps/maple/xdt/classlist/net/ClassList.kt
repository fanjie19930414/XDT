package com.kapok.apps.maple.xdt.classlist.net

import com.kapok.apps.maple.xdt.classlist.bean.*
import com.kapok.apps.maple.xdt.classlist.model.model_req.*
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kapok.apps.maple.xdt.usercenter.model.model_req.TeacherApplyJoinClassReq
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 课程表相关接口
 */
interface ClassList {
    // 班级列表家长端
    @GET("api/class/searchparentclasslist")
    fun getClassListParent(@Query("userid") userid: Int): Observable<BaseResponse<MutableList<ParentClassListBean>?>>

    // 班级列表老师端
    @GET("api/class/searchteacherclasslist")
    fun getClassListTeacher(@Query("isleader") isleader: Boolean, @Query("userid") userid: Int): Observable<BaseResponse<MutableList<ParentClassListBean>?>>

    // 老师撤回班级申请
    @POST("api/class/revoketeacherclassapply")
    fun cancelClassList(@Body req: CancelClassListTeacherReq): Observable<BaseResponse<String>>

    // 家长撤回班级申请
    @POST("api/class/revokeparentclassapply")
    fun cancelClassListParent(@Body req: CancelClassListTeacherReq): Observable<BaseResponse<String>>

    // 班级详情接口
    @POST("api/class/searchclassdetail")
    fun getClassDetailInfo(@Body req: ClassDetailReq): Observable<BaseResponse<ClassDetailInfoBean>>

    // 根据班级获取老师列表接口
    @GET("api/class/searchclassteacher")
    fun getClassTeacherList(@Query("classid") classId: Int): Observable<BaseResponse<MutableList<TeacherOutPutVOList>?>>

    // 转移班主任权限接口
    @POST("api/teacher/changeteacherheader")
    fun changeHeaderTeacher(@Body req: ChangeHeaderTeacherReq): Observable<BaseResponse<String>>

    // 解散班级接口
    @POST("api/teacher/updateclassstatus")
    fun dissolvedClass(@Body req: DissolvedClassReq): Observable<BaseResponse<String>>

    // 班级升学接口
    @POST("api/class/createclassbyoldclass")
    fun classUpdate(@Body req: ClassUpdateReq): Observable<BaseResponse<String>>

    // 获取班级资料教师端接口（代课教师或班主任）
    @POST("api/class/searchteacherclassdetail")
    fun getClassInfoTeacher(@Body req: ClassInfoTeacherReq): Observable<BaseResponse<ClassInfoBean>>

    // 获取班级资料家长端接口
    @POST("api/class/searchparentclassdetail")
    fun getClassInfoParent(@Body req: ClassInfoTeacherReq): Observable<BaseResponse<ClassInfoBean>>

    // 退出班级接口
    @POST("api/usercenter/quitClass")
    fun exitClass(@Body req: QuiteClassReq): Observable<BaseResponse<String>>

    // 编辑班级资料接口（班主任）
    @POST("api/class/updateclassdetail")
    fun updateClassInfo(@Body req: UpdateClassInfoReq): Observable<BaseResponse<String>>

    // 获取老师作业通知列表
    @POST("api/class/getTeacherWorkNoticeTeacherList")
    fun getTeacherWorkNoticeTeacherList(@Body req: TeacherWorkNoticeListReq): Observable<BaseResponse<HomeWorkNoticeBean>>

    // 获取家长作业通知列表
    @POST("api/class/getPatriarchWorkNoticeTeacherList")
    fun getParentWorkNoticeTeacherList(@Body req: TeacherWorkNoticeListReq): Observable<BaseResponse<HomeWorkNoticeBean>>
}