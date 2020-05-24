package com.kapok.apps.maple.xdt.addressbook.presenter.view

import com.kapok.apps.maple.xdt.addressbook.bean.AddressBookApplyListBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface AddressBookNewPersonView: BaseView {
    fun getApplyList(bean: AddressBookApplyListBean)

    fun approvalResult(msg: String)
}