package com.kapok.apps.maple.xdt.usercenter.model

import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SearchSchoolListBean
import io.reactivex.Observable

interface SchoolAndCityListModel {
    // 热门学校接口
    fun getSchoolHotList(
        city: String,
        name: String
    ): Observable<MutableList<NearBySchoolBean>?>

    // 附近学校接口
    fun getSchoolNearByList(
        city: String,
        latitude: String,
        longitude: String,
        name: String
    ): Observable<MutableList<NearBySchoolBean>?>

    // 搜索城市列表接口
    fun getCityList(name: String): Observable<MutableList<SearchCityListBean>?>

    // 搜索学校列表接口
    fun getSchoolList(
        cityName: String,
        pageIndex: Int,
        pageSize: Int,
        schoolName: String
    ): Observable<SearchSchoolListBean>
}