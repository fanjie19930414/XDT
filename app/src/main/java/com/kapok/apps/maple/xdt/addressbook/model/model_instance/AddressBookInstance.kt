package com.kapok.apps.maple.xdt.addressbook.model.model_instance

import com.kapok.apps.maple.xdt.addressbook.bean.*
import com.kapok.apps.maple.xdt.addressbook.model.AddressBookModel
import com.kapok.apps.maple.xdt.addressbook.model.model_req.ApprovalReq
import com.kapok.apps.maple.xdt.addressbook.model.model_req.DetachClassReq
import com.kapok.apps.maple.xdt.addressbook.net.AddressBook
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class AddressBookInstance: AddressBookModel {
    // 获取申请列表
    override fun getApplyList(
        classId: Int,
        userId: Int
    ): Observable<AddressBookApplyListBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .getApplyList(classId, userId)
            .convert()
    }

    // 老师获取班级通讯录列表接口
    override fun getAddressBookDetails(classId: Int, userId: Int): Observable<AddressBookDetails> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .getTeacherContractList(classId, userId)
            .convert()
    }

    // 获取孩子详情接口
    override fun getChildDetail(
        classId: Int,
        studentId: Int,
        userId: Int
    ): Observable<AddressBookChildDetailBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .getChildDetail(classId, studentId, userId)
            .convert()
    }

    // 获取家长详情接口
    override fun getParentDetail(
        classId: Int,
        patriarchId: Int,
        userId: Int
    ): Observable<AddressBookParentDetailBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .getParentDetail(classId, patriarchId, userId)
            .convert()
    }

    // 获取老师详情接口
    override fun getTeacherDetail(
        classId: Int,
        teacherId: Int,
        userId: Int
    ): Observable<AddressBookTeacherDetailBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .getTeacherDetail(classId, teacherId, userId)
            .convert()
    }

    // 同意申请接口
    override fun approvalApply(
        approvalResult: Int,
        approvalUserId: Int,
        classId: Int,
        userId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .approval(ApprovalReq(approvalResult, approvalUserId, classId, userId))
            .convertNoResult()
    }

    // 移除班级接口
    override fun detachClass(classId: Int, removeUserId: Int, userId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(AddressBook::class.java)
            .detachClass(DetachClassReq(classId, removeUserId, userId))
            .convertNoResult()
    }
}