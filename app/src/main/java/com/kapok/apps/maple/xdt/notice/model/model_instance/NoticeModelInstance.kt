package com.kapok.apps.maple.xdt.notice.model.model_instance

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean
import com.kapok.apps.maple.xdt.notice.bean.*
import com.kapok.apps.maple.xdt.notice.model.NoticeModel
import com.kapok.apps.maple.xdt.notice.model.model_req.*
import com.kapok.apps.maple.xdt.notice.net.Notice
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class NoticeModelInstance : NoticeModel {
    // 获取班级老师
    override fun getTeacherInClasses(classId: Int): Observable<MutableList<TeacherInClassesBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .getTeacherInClasses(classId)
            .convert()
    }

    // 创建通知
    override fun createNotice(
        content: String,
        images: String,
        isReceipt: Boolean,
        publishUserId: Int,
        receiptContent: MutableList<String>,
        receiptType: Int,
        studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
        teacherDetailVoList: MutableList<NoticeTeacherDetailBean>,
        title: String
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .createNotice(
                CreateNoticeReq(
                    content,
                    images,
                    isReceipt,
                    publishUserId,
                    receiptContent,
                    receiptType,
                    studentDetailVoList,
                    teacherDetailVoList,
                    title
                )
            )
            .convertNoResult()
    }

    // 班级通知列表班主任端
    override fun getNoticeListTeacher(
        classId: Int,
        identityType: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int,
        receiptStatus: Int,
        state: Int,
        submitStatus: Int,
        userId: Int
    ): Observable<NoticeListTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .getNoticeListTeacher(
                NoticeListTeacherReq(
                    classId,
                    identityType,
                    onlySelfWork,
                    pageNo,
                    pageSize,
                    pubEndTime,
                    pubStartTime,
                    readStatus,
                    receiptStatus,
                    state,
                    submitStatus,
                    userId
                )
            ).convert()
    }

    // 查看通知班主任端
    override fun checkNoticeTeacher(
        classId: Int,
        teacherId: Int,
        workId: Int
    ): Observable<NoticeDetailTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .getNoticeDetailTeacher(classId,teacherId, workId)
            .convert()
    }

    // 查看通知统计
    override fun getTeacherNoticeReceiptList(
        receiptState: Int,
        teacherId: Int,
        workId: Int
    ): Observable<MutableList<NoticeDataStatisticsBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .getNoticeReceiptList(receiptState, teacherId, workId)
            .convert()
    }

    // 再次发布
    override fun publishNoticeAgain(teacherId: Int, workId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .rePubNotice(RepubNoticeReq(teacherId, workId))
            .convertNoResult()
    }

    // 一键提醒
    override fun remindNotice(
        studentIds: MutableList<Int>,
        teacherId: Int,
        workId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .remindNotice(RemindNoticeReq(studentIds, teacherId, workId))
            .convertNoResult()
    }

    // 删除通知
    override fun delectNotice(teacherId: Int, workId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .deleteNotice(DeleteNoticeReq(teacherId, workId))
            .convertNoResult()
    }

    // 班级通知列表家长端
    override fun getNoticeListParent(
        classId: Int,
        identityType: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int,
        receiptStatus: Int,
        state: Int,
        submitStatus: Int,
        userId: Int
    ): Observable<NoticeListTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .getNoticeListParent(NoticeListTeacherReq(classId, identityType, onlySelfWork, pageNo, pageSize, pubEndTime, pubStartTime, readStatus, receiptStatus, state, submitStatus, userId))
            .convert()
    }

    // 查看班级通知家长端
    override fun checkNoticeParent(
        classId: Int,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<NoticeDetailParentBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .getNoticeDetailParent(classId,patriarchId, studentId, workId)
            .convert()
    }

    // 提交回执
    override fun submitReceive(
        receiptId: Int,
        studentId: Int,
        userId: Int,
        workId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(Notice::class.java)
            .submitReceive(SubmitReceiveReq(receiptId, studentId, userId, workId))
            .convertNoResult()
    }
}