package com.kapok.apps.maple.xdt.home.model.model_instance

import com.kapok.apps.maple.xdt.home.bean.*
import com.kapok.apps.maple.xdt.home.model.HomeModel
import com.kapok.apps.maple.xdt.home.model.model_req.NewsListReq
import com.kapok.apps.maple.xdt.home.model.model_req.ReportSuggest
import com.kapok.apps.maple.xdt.home.model.model_req.UnbindChildReq
import com.kapok.apps.maple.xdt.home.model.model_req.UpdateUserInfoReq
import com.kapok.apps.maple.xdt.home.net.HomeNet
import com.kotlin.baselibrary.commen.BaseOSSBean
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertBoolean
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class HomeModelInstance : HomeModel {
    // 获取我的孩子接口
    override fun getMyChildren(userid: Int): Observable<MutableList<MyChildrenBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getMyChilderen(userid)
            .convert()
    }

    // 获取个人信息
    override fun getUserInfo(userid: Int, identitytype: Int): Observable<UserInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getUserInfo(userid, identitytype)
            .convert()
    }

    // 获取职业
    override fun getPerfession(): Observable<MutableList<EducationOrProfessionBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getPerfession()
            .convert()
    }

    // 获取学历
    override fun getEducation(): Observable<MutableList<EducationOrProfessionBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getEducation()
            .convert()
    }

    // 更新用户信息
    override fun updateUserInfo(
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
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .updateUserInfo(
                UpdateUserInfoReq(
                    avatar,
                    birthday,
                    education,
                    identityType,
                    job,
                    realName,
                    schoolId,
                    schoolName,
                    sex,
                    subjectId,
                    subjectName,
                    telephone,
                    userId
                )
            )
            .convertNoResult()
    }

    // 获取孩子信息
    override fun getChildInfo(studentId: Int): Observable<ChildInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getChildInfo(studentId)
            .convert()
    }

    // 家长解绑学生
    override fun unbindChild(patriarchId: Int, studentId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .unbindChild(UnbindChildReq(patriarchId, studentId))
            .convert()
    }

    // 获取角色列表接口
    override fun getIdentityListBean(userid: Int): Observable<MutableList<IdentityBean>> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getIdentityListInfo(userid)
            .convert()
    }

    // 获取OSS STSToken接口
    override fun getOSSToken(objectName: String): Observable<BaseOSSBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getOSSToken(objectName)
            .convert()
    }

    // 获取新闻列表接口
    override fun getNewsList(pageNo: Int, pageSize: Int): Observable<NewsBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .getNewsList(NewsListReq(pageNo, pageSize))
            .convert()
    }

    // 切换身份接口
    override fun changeIdentity(identitytype: Int, userid: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .changeIdentity(identitytype, userid)
            .convertNoResult()
    }

    // 提交一键反馈接口
    override fun reportSuggest(content: String, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeNet::class.java)
            .reportSuggest(ReportSuggest(content, userId))
            .convertNoResult()
    }
}