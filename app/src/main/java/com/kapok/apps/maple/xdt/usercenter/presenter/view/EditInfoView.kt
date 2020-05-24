package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SaveStudentIdBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface EditInfoView : BaseView {
    fun saveChildSuccessful(studentId: SaveStudentIdBean)

    fun saveParentSuccessful(boolean: Boolean)

    fun getRelationList(dataList : MutableList<RelationListBean>?)
}