package com.kapok.apps.maple.xdt.addressbook.presenter.view

import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetailBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddressBookTeacherDetailView: BaseView {
    fun getTeacherDetail(bean: AddressBookTeacherDetailBean)

    fun detachClass(msg: String)
}