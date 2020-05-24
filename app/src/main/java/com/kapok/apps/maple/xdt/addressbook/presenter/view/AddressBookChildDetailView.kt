package com.kapok.apps.maple.xdt.addressbook.presenter.view

import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookChildDetailBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddressBookChildDetailView : BaseView {
    fun getChildDetail(bean: AddressBookChildDetailBean)

    fun detachClass(msg: String)
}