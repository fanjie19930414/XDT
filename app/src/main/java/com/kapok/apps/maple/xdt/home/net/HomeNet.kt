package com.kapok.apps.maple.xdt.home.net

import com.kapok.apps.maple.xdt.home.bean.*
import com.kapok.apps.maple.xdt.home.model.model_req.NewsListReq
import com.kapok.apps.maple.xdt.home.model.model_req.ReportSuggest
import com.kapok.apps.maple.xdt.home.model.model_req.UnbindChildReq
import com.kapok.apps.maple.xdt.home.model.model_req.UpdateUserInfoReq
import com.kotlin.baselibrary.commen.BaseOSSBean
import com.kotlin.baselibrary.net.BaseResponse
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HomeNet {
    // 我的孩子接口
    @GET("api/usercenter/getmychilds")
    fun getMyChilderen(@Query("userid") userid: Int): Observable<BaseResponse<MutableList<MyChildrenBean>?>>

    // 个人信息接口
    @GET("api/usercenter/getuserinfo")
    fun getUserInfo(@Query("userid") userid: Int, @Query("identitytype") identitytype: Int): Observable<BaseResponse<UserInfoBean>>

    // 获取职业接口
    @GET("api/usercenter/getjob")
    fun getPerfession(): Observable<BaseResponse<MutableList<EducationOrProfessionBean>?>>

    // 获取学历接口
    @GET("api/usercenter/geteducation")
    fun getEducation(): Observable<BaseResponse<MutableList<EducationOrProfessionBean>?>>

    // 更新用户信息
    @POST("api/usercenter/updateuserinfo")
    fun updateUserInfo(@Body req: UpdateUserInfoReq): Observable<BaseResponse<String>>

    // 获取孩子信息
    @GET("api/usercenter/getstudentinfo")
    fun getChildInfo(@Query("studentId") studentId: Int): Observable<BaseResponse<ChildInfoBean>>

    // 家长解绑学生
    @POST("api/usercenter/unbindstudent")
    fun unbindChild(@Body req: UnbindChildReq): Observable<BaseResponse<String>>

    // 获取角色列表接口
    @GET("api/usercenter/getidentitytype")
    fun getIdentityListInfo(@Query("userid") userid: Int): Observable<BaseResponse<MutableList<IdentityBean>>>

    // 获取OSS STSToken接口
    @GET("/ceiba.web.api/api/common/oss/ticker")
    fun getOSSToken(@Query("userDir") objectName: String): Observable<BaseResponse<BaseOSSBean>>

    // 获取新闻列表接口
    @POST("api/news/getNewsList")
    fun getNewsList(@Body req: NewsListReq): Observable<BaseResponse<NewsBean>>

    // 切换身份接口
    @GET("api/usercenter/changeIdentitytype")
    fun changeIdentity(@Query("identitytype") identitytype: Int, @Query("userid") userid: Int): Observable<BaseResponse<String>>

    // 意见反馈接口
    @POST("api/user/auth/submitSuggestion")
    fun reportSuggest(@Body req: ReportSuggest): Observable<BaseResponse<String>>
}