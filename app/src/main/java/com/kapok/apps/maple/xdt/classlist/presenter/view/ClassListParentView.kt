package com.kapok.apps.maple.xdt.classlist.presenter.view

import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface ClassListParentView : BaseView {
    // 班级列表家长端
    fun getClassListParent(bean: MutableList<ParentClassListBean>?)

    // 取消申请加入班级
    fun cancelClassList(msg : String)
}