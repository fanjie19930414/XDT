package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.home.bean.IdentityBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kotlin.baselibrary.commen.BaseOSSBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface UserInfoView : BaseView {
    fun getUserInfoBean(bean: UserInfoBean)

    fun getEducationList(bean: MutableList<EducationOrProfessionBean>?)

    fun getPerfessionList(bean: MutableList<EducationOrProfessionBean>?)

    fun updateUserInfoResult(msg: String)

    fun getSubjectList(bean: MutableList<SubjectListBean>?)

    fun getIdentityInfo(bean: MutableList<IdentityBean>)

    fun getOSSToken(bean: BaseOSSBean,type: Int)

    fun changeIdentity(msg: String)
}