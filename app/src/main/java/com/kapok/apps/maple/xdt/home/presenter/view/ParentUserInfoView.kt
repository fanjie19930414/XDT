package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface ParentUserInfoView: BaseView {
    fun getUserInfoBean(bean: UserInfoBean)

    fun getRelationList(dataList: MutableList<RelationListBean>?)

    fun unBindChild(msg: String)
}