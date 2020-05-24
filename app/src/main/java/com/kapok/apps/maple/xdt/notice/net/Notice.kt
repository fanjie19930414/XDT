package com.kapok.apps.maple.xdt.notice.net

import com.kapok.apps.maple.xdt.notice.bean.*
import com.kapok.apps.maple.xdt.notice.model.model_req.*
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Notice {
    // 获取班级下老师
    @GET("api/notice/getClassTeachers")
    fun getTeacherInClasses(@Query("classId") classId: Int): Observable<BaseResponse<MutableList<TeacherInClassesBean>?>>

    // 发布通知
    @POST("api/notice/createNotice")
    fun createNotice(@Body req: CreateNoticeReq): Observable<BaseResponse<String>>

    // 班级通知列表班主任端
    @POST("api/notice/getTeacherNoticeList")
    fun getNoticeListTeacher(@Body req: NoticeListTeacherReq): Observable<BaseResponse<NoticeListTeacherBean>>

    // 查看通知班主任端口
    @GET("api/notice/getTeacherNoticeDetail")
    fun getNoticeDetailTeacher(@Query("classId") classId: Int,@Query("teacherId") teacherId: Int, @Query("workId") workId: Int): Observable<BaseResponse<NoticeDetailTeacherBean>>

    // 查看通知数据统计
    @GET("api/notice/getTeacherNoticeReceiptList")
    fun getNoticeReceiptList(
        @Query("receiptState") receiptState: Int, @Query("teacherId") teacherId: Int, @Query(
            "workId"
        ) workId: Int
    ): Observable<BaseResponse<MutableList<NoticeDataStatisticsBean>?>>

    // 再次发布通知
    @POST("api/notice/rePubNotice")
    fun rePubNotice(@Body req: RepubNoticeReq): Observable<BaseResponse<String>>

    // 通知一键提醒
    @POST("api/notice/batchNotice")
    fun remindNotice(@Body req: RemindNoticeReq): Observable<BaseResponse<String>>

    // 删除通知
    @POST("api/notice/deleteNotice")
    fun deleteNotice(@Body req: DeleteNoticeReq): Observable<BaseResponse<String>>

    // 班级通知列表家长端
    @POST("api/notice/getPatriarchNoticeList")
    fun getNoticeListParent(@Body req: NoticeListTeacherReq): Observable<BaseResponse<NoticeListTeacherBean>>

    // 查看通知家长端口
    @GET("api/notice/getStudentReceiptReplyDetail")
    fun getNoticeDetailParent(
        @Query("classId") classId: Int,
        @Query("patriarchId") patriarchId: Int, @Query("studentId") studentId: Int, @Query(
            "workId"
        ) workId: Int
    ): Observable<BaseResponse<NoticeDetailParentBean>>

    // 提交回执
    @POST("api/notice/submitNoticeReceipt")
    fun submitReceive(@Body req: SubmitReceiveReq): Observable<BaseResponse<String>>
}