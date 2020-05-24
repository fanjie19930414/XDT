package com.kapok.apps.maple.xdt.usercenter.presenter.view

import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface TeacherEditInfoView : BaseView {
    fun saveSuccessful(boolean: Boolean)

    fun getSubjectList(dataList: MutableList<SubjectListBean>?)
}