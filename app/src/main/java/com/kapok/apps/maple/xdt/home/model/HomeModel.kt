package com.kapok.apps.maple.xdt.home.model

import com.kapok.apps.maple.xdt.home.bean.*
import com.kotlin.baselibrary.commen.BaseOSSBean
import io.reactivex.Observable

interface HomeModel {
    // 获取我的孩子接口
    fun getMyChildren(userid: Int): Observable<MutableList<MyChildrenBean>?>

    // 个人信息接口
    fun getUserInfo(userid: Int, identitytype: Int): Observable<UserInfoBean>

    // 获取学历
    fun getEducation(): Observable<MutableList<EducationOrProfessionBean>?>

    // 获取职业
    fun getPerfession(): Observable<MutableList<EducationOrProfessionBean>?>

    // 更新用户信息
    fun updateUserInfo(
        avatar: String,
        birthday: String,
        education: String,
        identityType: Int,
        job: String,
        realName: String,
        schoolId: Int,
        schoolName: String,
        sex: String,
        subjectId: Int,
        subjectName: String,
        telephone: String,
        userId: Int
    ): Observable<String>

    // 获取学生信息
    fun getChildInfo(studentId: Int): Observable<ChildInfoBean>

    // 家长解绑学生
    fun unbindChild(patriarchId: Int, studentId: Int):Observable<String>

    // 获取角色列表接口
    fun getIdentityListBean(userid: Int): Observable<MutableList<IdentityBean>>

    // 获取OSS STSToken接口
    fun getOSSToken(objectName: String): Observable<BaseOSSBean>

    // 获取新闻列表接口
    fun getNewsList(pageNo: Int,pageSize: Int): Observable<NewsBean>

    // 切换身份接口
    fun changeIdentity(identitytype: Int,userid: Int): Observable<String>

    // 意见反馈接口
    fun reportSuggest(content: String,userId: Int): Observable<String>
}