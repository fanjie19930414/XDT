package com.kapok.apps.maple.xdt.homework.model

import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.homework.bean.*
import io.reactivex.Observable

interface HomeWorkModel {
    // 获取老师所在班级
    fun getTeacherInClasses(teacherId: String): Observable<MutableList<TeacherInClasses>?>

    // 获取班级下学生列表
    fun getStudentInClass(classId: Int, teacherId: Int): Observable<MutableList<StudentInClasses>?>

    // 获取作业类型
    fun getHomeWorkType(): Observable<MutableList<EducationOrProfessionBean>?>

    // 创建作业
    fun createHomeWork(
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
    ): Observable<String>

    // 班级作业列表教师端
    fun getHomeWorkListTeacher(
        classId: Int,
        onlySelfWork: Boolean,
        pageNo: Int,
        pageSize: Int,
        pubEndTime: String,
        pubStartTime: String,
        state: String,
        submitStatus: String,
        teacherId: String
    ): Observable<HomeWorkListTeacherBean>

    // 班级作业列表家长端
    fun getHomeWorkListParent(
        classId: Int,
        pageNo: Int,
        pageSize: Int,
        patriachId: String,
        pubEndTime: String,
        pubStartTime: String,
        state: String,
        submitStatus: String
    ): Observable<HomeWorkListTeacherBean>

    // 查看作业（教师）
    fun checkHomeWorkTeacher(
        teacherId: Int,
        workId: Int
    ): Observable<CheckHomeWorkTeacherBean>

    // 老师获取班级提交作业列表
    fun getCommitHomeWorkListTeacher(
        submitState: Int, teacherId: Int, workId: Int
    ): Observable<MutableList<CommitHomeWorkClassInfoBean>?>

    // 修改作业截止时间
    fun editHomeWorkTeacher(
        deadline: String,
        workId: Int
    ): Observable<String>

    // 结束作业
    fun finishHomeWorkTeacher(
        workId: Int
    ): Observable<String>

    // 删除作业
    fun deleteHomeWorkTeacher(
        teacherId: Int,
        workId: Int
    ): Observable<String>

    // 一键提醒
    fun remindHomeWorkTeacher(
        studentIds: MutableList<Int>,
        teacherId: Int,
        workId: Int
    ): Observable<String>

    // 查看作业(家长已提交的)
    fun checkHomeWorkParentCommit(
        studentId: Int,
        teacherId: Int,
        workId: Int
    ): Observable<CheckHomeWorkParentBean?>

    // 家长查看作业
    fun checkHomeWorkParent(
        classId: Int,
        patriarchId: Int,
        workId: Int
    ): Observable<CheckHomeWorkTeacherBean>

    // 保存作业(家长)
    fun saveHomeWorkParent(
        content: String,
        images: String,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<String>

    // 提交作业(家长)
    fun publishHomeWorkParent(
        content: String,
        images: String,
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<String>

    // 获取教师点评
    fun getTeacherComment(
        patriarchId: Int,
        studentId: Int,
        workId: Int
    ): Observable<MutableList<TeacherCommentParentBean>?>

    // 获取作业作答
    fun getWorkAnswer(
        classId: Int,
        studentId: Int,
        workId: Int
    ): Observable<HomeWorkAnswerBean>

    // 获取老师评论(老师端)
    fun getHomeWorkComment(
        studentId: Int,
        teacherId: Int,
        workId: Int
    ): Observable<MutableList<TeacherCommentParentBean>?>

    // 获取常用评语
    fun getCommonComment(teacherId: Int): Observable<MutableList<CommonComment>?>

    // 提交教师评语
    fun createTeacherComment(
        content: String,
        patriarchId: Int,
        studentId: Int,
        teacherId: Int,
        workAnswerId: Int,
        workId: Int
    ): Observable<String>

    // 创建评论
    fun createComment(
        content: String,
        teacherId: Int
    ): Observable<String>
}