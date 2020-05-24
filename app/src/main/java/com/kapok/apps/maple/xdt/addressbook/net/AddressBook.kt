package com.kapok.apps.maple.xdt.addressbook.net

import com.kapok.apps.maple.xdt.addressbook.bean.*
import com.kapok.apps.maple.xdt.addressbook.model.model_req.ApprovalReq
import com.kapok.apps.maple.xdt.addressbook.model.model_req.DetachClassReq
import com.kapok.apps.maple.xdt.notice.model.model_req.RepubNoticeReq
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AddressBook {
    // 获取申请列表
    @GET("api/contract/getApplyList")
    fun getApplyList(@Query("classId") classId: Int, @Query("userId") userId: Int):
            Observable<BaseResponse<AddressBookApplyListBean>>

    // 老师获取班级通讯录列表接口
    @GET("api/contract/getContractList")
    fun getTeacherContractList(@Query("classId") classId: Int, @Query("userId") userId: Int):
            Observable<BaseResponse<AddressBookDetails>>

    // 获取孩子详情接口
    @GET("api/contract/getStudentDetail")
    fun getChildDetail(@Query("classId") classId: Int, @Query("studentId") studentId: Int, @Query("userId") userId: Int):
            Observable<BaseResponse<AddressBookChildDetailBean>>

    // 获取家长详情接口
    @GET("api/contract/getPatriarchDetail")
    fun getParentDetail(@Query("classId")classId: Int,@Query("patriarchId")patriarchId: Int,@Query("userId")userId: Int):
            Observable<BaseResponse<AddressBookParentDetailBean>>

    // 获取老师详情接口
    @GET("api/contract/getTeacherDetail")
    fun getTeacherDetail(@Query("classId")classId: Int,@Query("teacherId")teacherId:Int,@Query("userId")userId: Int):
            Observable<BaseResponse<AddressBookTeacherDetailBean>>

    // 同意/拒绝申请
    @POST("api/contract/approval")
    fun approval(@Body req: ApprovalReq): Observable<BaseResponse<String>>

    // 移除班级
    @POST("api/contract/remove")
    fun detachClass(@Body req: DetachClassReq): Observable<BaseResponse<String>>
}