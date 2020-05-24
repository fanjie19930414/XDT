package com.kapok.apps.maple.xdt.addressbook.bean

data class AddressBookChildDetailBean(
    val avatar: String?,
    val identityType: Int,
    val patriarchs: MutableList<AddressBookChildParentBean>,
    val realName: String,
    val relation: String,
    val sex: String,
    val subjectId: Int,
    val subjectName: String,
    val telephone: String,
    val userId: Int,
    val birthday: String
)