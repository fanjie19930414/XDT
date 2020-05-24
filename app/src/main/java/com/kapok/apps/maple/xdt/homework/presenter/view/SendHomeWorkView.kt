package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface SendHomeWorkView: BaseView {
    fun getWorkType(list: MutableList<EducationOrProfessionBean>?)

    fun getSubjectList(subjectList: MutableList<SubjectListBean>?)

    fun createHomeWork(msg: String)
}