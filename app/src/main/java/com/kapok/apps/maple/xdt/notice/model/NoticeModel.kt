package com.kapok.apps.maple.xdt.notice.model

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean
import com.kapok.apps.maple.xdt.notice.bean.*
import io.reactivex.Observable

interface NoticeModel {
    // 获取班级老师
    fun getTeacherInClasses(classId: Int): Observable<MutableList<TeacherInClassesBean>?>

    // 创建通知
    fun createNotice(
        content: String,
        images: String,
        isReceipt: Boolean,
        publishUserId: Int,
        receiptContent: MutableList<String>,
        receiptType: Int,
        studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
        teacherDetailVoList: MutableList<NoticeTeacherDetailBean>,
        title: String
    ): Observable<String>

    // 班级通知列表班主任端
    fun getNoticeListTeacher(
        classId: Int,
        identityType: Int, // 0学生 1家长 2老师
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int, // 1未读 2已读
        receiptStatus: Int,// 1 部分未完成 2 全员已完成
        state: Int, // 1 未发布 2进行中 3已结束
        submitStatus: Int,// 1(家长 未提交/部分未完成) 2(家长 已提交/全员已完成)
        userId: Int
    ): Observable<NoticeListTeacherBean>

    // 查看通知班主任端
    fun checkNoticeTeacher(
        classId: Int,
        teacherId: Int,
        workId: Int
    ): Observable<NoticeDetailTeacherBean>

    // 查看通知统计
    fun getTeacherNoticeReceiptList(
        receiptState: Int,
        teacherId: Int,
        workId: Int
    ): Observable<MutableList<NoticeDataStatisticsBean>?>

    // 再次发布
    fun publishNoticeAgain(
        teacherId: Int,
        workId: Int
    ): Observable<String>

    // 一键提醒通知
    fun remindNotice(
        studentIds: MutableList<Int>,
        teacherId: Int,
        workId: Int
    ): Observable<String>

    // 删除通知
    fun delectNotice(
        teacherId: Int,
        workId: Int
    ): Observable<String>

    // 班级通知列表班主任端
    fun getNoticeListParent(
        classId: Int,
        identityType: Int, // 0学生 1家长 2老师
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        readStatus: Int, // 1未读 2已读
        receiptStatus: Int,// 1 部分未完成 2 全员已完成
        state: Int, // 1 未发布 2进行中 3已结束
        submitStatus: Int,// 1(家长 未提交/部分未完成) 2(家长 已提交/全员已完成)
        userId: Int
    ): Observable<NoticeListTeacherBean>

    // 查看通知家长端
    fun checkNoticeParent(
        classId: Int,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<NoticeDetailParentBean>

    // 提交回执
    fun submitReceive(
        receiptId: Int,
        studentId: Int,
        userId: Int,
        workId: Int
    ): Observable<String>
}