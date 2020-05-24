package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SaveStudentIdBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddChildView: BaseView {
    fun getRelationList(dataList: MutableList<RelationListBean>?)

    fun saveChildSuccessful(studentId: SaveStudentIdBean)

    fun saveParentSuccessful(boolean: Boolean)
}