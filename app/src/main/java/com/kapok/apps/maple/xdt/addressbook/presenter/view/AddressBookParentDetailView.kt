package com.kapok.apps.maple.xdt.addressbook.presenter.view

import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookParentDetailBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddressBookParentDetailView : BaseView {
    fun getParentDetail(bean: AddressBookParentDetailBean)
}