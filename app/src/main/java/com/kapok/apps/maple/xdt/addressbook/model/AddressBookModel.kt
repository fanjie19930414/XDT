package com.kapok.apps.maple.xdt.addressbook.model

import com.kapok.apps.maple.xdt.addressbook.bean.*
import io.reactivex.Observable

interface AddressBookModel {
    // 获取申请列表
    fun getApplyList(classId: Int,userId: Int): Observable<AddressBookApplyListBean>

    // 老师获取班级通讯录列表接口
    fun getAddressBookDetails(classId: Int,userId: Int): Observable<AddressBookDetails>

    // 获取孩子详情接口
    fun getChildDetail(classId: Int,studentId: Int,userId: Int): Observable<AddressBookChildDetailBean>

    // 获取家长详情接口
    fun getParentDetail(classId: Int,patriarchId: Int,userId: Int):Observable<AddressBookParentDetailBean>

    // 获取老师详情接口
    fun getTeacherDetail(classId: Int,teacherId: Int,userId: Int): Observable<AddressBookTeacherDetailBean>

    // 同意申请接口
    fun approvalApply(approvalResult: Int,approvalUserId: Int,classId: Int,userId: Int): Observable<String>

    // 移除班级接口
    fun detachClass(classId: Int, removeUserId: Int, userId: Int): Observable<String>
}