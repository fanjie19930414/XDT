package com.kapok.apps.maple.xdt.homework.model.model_instance

import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.homework.bean.*
import com.kapok.apps.maple.xdt.homework.model.HomeWorkModel
import com.kapok.apps.maple.xdt.homework.model.model_req.*
import com.kapok.apps.maple.xdt.homework.net.HomeWork
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class HomeWorkModelInstance : HomeWorkModel {
    // 获取老师所在班级
    override fun getTeacherInClasses(teacherId: String): Observable<MutableList<TeacherInClasses>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getTeacherInClasses(teacherId)
            .convert()
    }

    // 获取班级下学生列表
    override fun getStudentInClass(
        classId: Int,
        teacherId: Int
    ): Observable<MutableList<StudentInClasses>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getStudentInClass(classId, teacherId)
            .convert()
    }

    // 获取作业类型
    override fun getHomeWorkType(): Observable<MutableList<EducationOrProfessionBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getWorkType()
            .convert()
    }

    // 创建作业
    override fun createHomeWork(
        content: String,
        deadline: String,
        images: String,
        state: Int,
        studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
        subjectId: Int,
        subjectName: String,
        teacherId: Int,
        title: String,
        videoUri: String,
        workType: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .createHomeWork(
                CreateHomeWorkReq(
                    content,
                    deadline,
                    images,
                    state,
                    studentDetailVoList,
                    subjectId,
                    subjectName,
                    teacherId,
                    title,
                    videoUri,
                    workType
                )
            )
            .convertNoResult()
    }

    // 班级作业列表教师端
    override fun getHomeWorkListTeacher(
        classId: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        state: String,
        submitStatus: String,
        teacherId: String
    ): Observable<HomeWorkListTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getHomeWorkListTeacher(
                HomeWorkListTeacherReq(
                    classId,
                    onlySelfWork,
                    pageNo,
                    pageSize,
                    pubEndTime,
                    pubStartTime,
                    state,
                    submitStatus,
                    teacherId
                )
            )
            .convert()
    }

    // 班级列表家长端
    override fun getHomeWorkListParent(
        classId: Int,
        pageNo: Int,
        pageSize: Int,
        patriachId: String,
        pubEndTime: String,
        pubStartTime: String,
        state: String,
        submitStatus: String
    ): Observable<HomeWorkListTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getHomeWorkListParent(
                HomeWorkListParentReq(
                    classId,
                    pageNo,
                    pageSize,
                    patriachId,
                    pubEndTime,
                    pubStartTime,
                    state,
                    submitStatus
                )
            )
            .convert()
    }

    // 查看作业教师
    override fun checkHomeWorkTeacher(
        teacherId: Int,
        workId: Int
    ): Observable<CheckHomeWorkTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .checkHomeWorkTeacher(teacherId, workId)
            .convert()
    }

    // 老师获取班级提交作业列表
    override fun getCommitHomeWorkListTeacher(
        submitState: Int,
        teacherId: Int,
        workId: Int
    ): Observable<MutableList<CommitHomeWorkClassInfoBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getCommitHomeWorkList(submitState, teacherId, workId)
            .convert()
    }

    // 修改作业截止时间
    override fun editHomeWorkTeacher(deadline: String, workId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .editHomeWorkTime(EditTimeHomeWorkReq(deadline, workId))
            .convertNoResult()
    }

    // 立即结束作业
    override fun finishHomeWorkTeacher(workId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .finishHomeWork(FinishHomeWorkReq(workId))
            .convertNoResult()
    }

    // 删除作业
    override fun deleteHomeWorkTeacher(teacherId: Int, workId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .deleteHomeWork(DeleteHomeWorkReq(teacherId, workId))
            .convertNoResult()
    }

    // 一键提醒
    override fun remindHomeWorkTeacher(
        studentIds: MutableList<Int>,
        teacherId: Int,
        workId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .remindHomeWork(RemindHomeWorkReq(studentIds, teacherId, workId))
            .convertNoResult()
    }

    // 查看作业家长(已提交的)
    override fun checkHomeWorkParentCommit(
        studentId: Int,
        teacherId: Int,
        workId: Int
    ): Observable<CheckHomeWorkParentBean?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .checkHomeWorkParentCommit(studentId, teacherId, workId)
            .convert()
    }

    // 查看作业家长
    override fun checkHomeWorkParent(
        classId: Int,
        patriarchId: Int,
        workId: Int
    ): Observable<CheckHomeWorkTeacherBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .checkHomeWorkParent(classId, patriarchId, workId)
            .convert()
    }

    // 保存作业家长
    override fun saveHomeWorkParent(
        content: String,
        images: String,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .saveHomeWorkParent(SaveHomeWorkReq(content, images, patriarchId, studentId, workId))
            .convertNoResult()
    }

    // 提交作业(家长)
    override fun publishHomeWorkParent(
        content: String,
        images: String,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .publishHomeWorkParent(SaveHomeWorkReq(content, images, patriarchId, studentId, workId))
            .convertNoResult()
    }

    // 获取教师评论(家长端)
    override fun getTeacherComment(
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<MutableList<TeacherCommentParentBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getTeacherCommentParent(patriarchId, studentId, workId)
            .convert()
    }

    // 获取作业作答
    override fun getWorkAnswer(
        classId: Int,
        studentId: Int,
        workId: Int
    ): Observable<HomeWorkAnswerBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getHomeWorkAnswer(classId, studentId, workId)
            .convert()
    }

    // 获取教师评论(老师端)
    override fun getHomeWorkComment(
        studentId: Int,
        teacherId: Int,
        workId: Int
    ): Observable<MutableList<TeacherCommentParentBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getHomeWorkComment(studentId, teacherId, workId)
            .convert()
    }

    // 获取常用评论
    override fun getCommonComment(teacherId: Int): Observable<MutableList<CommonComment>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .getCommonComment(teacherId)
            .convert()
    }

    // 提交教师评论
    override fun createTeacherComment(
        content: String,
        patriarchId: Int,
        studentId: Int,
        teacherId: Int,
        workAnswerId: Int,
        workId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .createTeacherComment(
                CreateTeacherCommentReq(
                    content,
                    patriarchId,
                    studentId,
                    teacherId,
                    workAnswerId,
                    workId
                )
            )
            .convert()
    }

    // 创建评论
    override fun createComment(content: String, teacherId: Int): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(HomeWork::class.java)
            .createComment(CreateCommentReq(content, teacherId))
            .convert()
    }
}