package com.kapok.apps.maple.xdt.homework.presenter.view

import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kotlin.baselibrary.presenter.view.BaseView

interface SettingCommentView : BaseView {
    fun getCommonComment(list: MutableList<CommonComment>?)

    fun createComment(msg: String)
}