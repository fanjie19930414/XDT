package com.kapok.apps.maple.xdt.usercenter.net

import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SearchSchoolListBean
import com.kapok.apps.maple.xdt.usercenter.model.model_req.NearBySchoolReq
import com.kapok.apps.maple.xdt.usercenter.model.model_req.SearchSchoolListReq
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// 学校 城市 相关接口
interface SchoolAndCityList {
    // 获取热门学校接口
    @GET("api/school/searchhotschoollist")
    fun hotSchoolList(@Query("city") city : String, @Query("name") name : String) : Observable<BaseResponse<MutableList<NearBySchoolBean>?>>

    // 获取附近学校接口
    @POST("api/school/searchnearschoollist")
    fun nearBySchoolList(@Body req: NearBySchoolReq): Observable<BaseResponse<MutableList<NearBySchoolBean>?>>

    // 获取城市列表接口
    @GET("api/school/searchcitylist")
    fun searchCityList(@Query("name") name: String): Observable<BaseResponse<MutableList<SearchCityListBean>?>>

    // 获取学校列表
    @POST("api/school/searchschoollist")
    fun searchSchoolList(@Body req: SearchSchoolListReq): Observable<BaseResponse<SearchSchoolListBean>>
}