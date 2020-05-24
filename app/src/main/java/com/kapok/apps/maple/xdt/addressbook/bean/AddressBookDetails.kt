package com.kapok.apps.maple.xdt.addressbook.bean

data class AddressBookDetails (
    val studentDetails: MutableList<AddressBookStudentDetails>?,
    val teacherDetails: MutableList<AddressBookTeacherDetails>?
)