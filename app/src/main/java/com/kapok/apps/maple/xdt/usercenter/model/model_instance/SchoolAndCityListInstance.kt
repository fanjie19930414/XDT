package com.kapok.apps.maple.xdt.usercenter.model.model_instance

import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SearchSchoolListBean
import com.kapok.apps.maple.xdt.usercenter.model.SchoolAndCityListModel
import com.kapok.apps.maple.xdt.usercenter.model.model_req.NearBySchoolReq
import com.kapok.apps.maple.xdt.usercenter.model.model_req.SearchSchoolListReq
import com.kapok.apps.maple.xdt.usercenter.net.SchoolAndCityList
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class SchoolAndCityListInstance : SchoolAndCityListModel {
    // 热门学校接口
    override fun getSchoolHotList(city: String, name: String): Observable<MutableList<NearBySchoolBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(SchoolAndCityList::class.java)
            .hotSchoolList(city, name)
            .convert()
    }

    // 附近学校接口
    override fun getSchoolNearByList(
        city: String, latitude: String, longitude: String, name: String
    ): Observable<MutableList<NearBySchoolBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(SchoolAndCityList::class.java)
            .nearBySchoolList(NearBySchoolReq(city, latitude, longitude, name))
            .convert()
    }

    // 搜索城市列表接口
    override fun getCityList(name: String): Observable<MutableList<SearchCityListBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(SchoolAndCityList::class.java)
            .searchCityList(name)
            .convert()
    }

    // 搜索学校列表接口
    override fun getSchoolList(
        cityName: String,
        pageIndex: Int,
        pageSize: Int,
        schoolName: String
    ): Observable<SearchSchoolListBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(SchoolAndCityList::class.java)
            .searchSchoolList(SearchSchoolListReq(cityName, pageIndex, pageSize, schoolName))
            .convert()
    }
}