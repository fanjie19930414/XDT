package com.kapok.apps.maple.xdt.addressbook.presenter.view

import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildDetailBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookTeacherDetailBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddressBookHandleDetailView: BaseView {
    fun getChildDetail(bean: AddressBookChildDetailBean)

    fun getTeacherDetail(bean: AddressBookTeacherDetailBean)

    fun approvalResult(msg: String)
}