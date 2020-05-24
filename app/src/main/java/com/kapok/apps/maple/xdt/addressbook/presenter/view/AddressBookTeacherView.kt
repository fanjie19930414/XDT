package com.kapok.apps.maple.xdt.addressbook.presenter.view

import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookApplyListBean
import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookDetails
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddressBookTeacherView: BaseView {
    fun getApplyList(list: AddressBookApplyListBean)

    fun getAddressDetails(bean: AddressBookDetails)
}