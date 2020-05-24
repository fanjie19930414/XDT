package com.kapok.apps.maple.xdt.homework.net

import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.homework.bean.*
import com.kapok.apps.maple.xdt.homework.model.model_req.*
import com.kotlin.baselibrary.net.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// 作业相关接口
interface HomeWork {
    // 获取老师所在的班级
    @GET("api/work/getTeacherClasses")
    fun getTeacherInClasses(@Query("teacherId") teacherId: String): Observable<BaseResponse<MutableList<TeacherInClasses>?>>

    // 获取班级下的学生列表
    @GET("api/work/getClassStudents")
    fun getStudentInClass(@Query("classId") classId: Int, @Query("teacherId") teacherId: Int): Observable<BaseResponse<MutableList<StudentInClasses>?>>

    // 获取作业类型
    @GET("api/work/getWorkType")
    fun getWorkType(): Observable<BaseResponse<MutableList<EducationOrProfessionBean>?>>

    // 创建作业
    @POST("api/work/createWork")
    fun createHomeWork(@Body req: CreateHomeWorkReq): Observable<BaseResponse<String>>

    // 班级作业列表教师端
    @POST("api/work/getTeacherWorkList")
    fun getHomeWorkListTeacher(@Body req: HomeWorkListTeacherReq): Observable<BaseResponse<HomeWorkListTeacherBean>>

    // 获取作业列表家长端
    @POST("api/work/getPatriarchWorkList")
    fun getHomeWorkListParent(@Body req: HomeWorkListParentReq): Observable<BaseResponse<HomeWorkListTeacherBean>>

    // 查看作业老师端
    @GET("api/work/getTeacherWorkDetail")
    fun checkHomeWorkTeacher(@Query("teacherId") teacherId: Int, @Query("workId") workId: Int): Observable<BaseResponse<CheckHomeWorkTeacherBean>>

    // 老师获取班级提交作业列表
    @GET("api/work/getTeacherWorkSubmitList")
    fun getCommitHomeWorkList(
        @Query("submitState") submitState: Int, @Query("teacherId") teacherId: Int, @Query(
            "workId"
        ) workId: Int
    ):
            Observable<BaseResponse<MutableList<CommitHomeWorkClassInfoBean>?>>

    // 修改截止时间
    @POST("api/work/updateWorkDeadLineVo")
    fun editHomeWorkTime(@Body req: EditTimeHomeWorkReq): Observable<BaseResponse<String>>

    // 结束作业
    @POST("api/work/quitNow")
    fun finishHomeWork(@Body req: FinishHomeWorkReq): Observable<BaseResponse<String>>

    // 删除作业
    @POST("api/work/deleteWork")
    fun deleteHomeWork(@Body req: DeleteHomeWorkReq): Observable<BaseResponse<String>>

    // 一键提醒
    @POST("api/work/batchNoticeWork")
    fun remindHomeWork(@Body req: RemindHomeWorkReq): Observable<BaseResponse<String>>

    // 查看作业(家长端已提交的)
    @GET("api/work/getStudentWorkAnswerDetail")
    fun checkHomeWorkParentCommit(
        @Query("studentId") studentId: Int, @Query("teacherId") teacherId: Int, @Query(
            "workId"
        ) workId: Int
    ): Observable<BaseResponse<CheckHomeWorkParentBean?>>

    // 查看作业(家长端)
    @GET("api/work/getPatriarchWorkDetail")
    fun checkHomeWorkParent(
        @Query("classId") classId: Int,
        @Query("patriarchId") patriarchId: Int,
        @Query("workId") workId: Int
    ): Observable<BaseResponse<CheckHomeWorkTeacherBean>>

    // 保存作业(家长端)
    @POST("api/work/saveWorkAnswer")
    fun saveHomeWorkParent(@Body req: SaveHomeWorkReq): Observable<BaseResponse<String>>

    // 提交作业(家长端)
    @POST("api/work/submitWorkAnswer")
    fun publishHomeWorkParent(@Body req: SaveHomeWorkReq): Observable<BaseResponse<String>>

    // 获取教师评论(家长端)
    @GET("api/work/getWorkCommentsForPatriarch")
    fun getTeacherCommentParent(
        @Query("patriarchId") patriarchId: Int,
        @Query("studentId") studentId: Int,
        @Query("workId") workId: Int
    ): Observable<BaseResponse<MutableList<TeacherCommentParentBean>?>>

    // 获取作业作答
    @GET("api/work/getWorkAnswer")
    fun getHomeWorkAnswer(
        @Query("classId") classId: Int,
        @Query("studentId") studentId: Int,
        @Query("workId") workId: Int
    ): Observable<BaseResponse<HomeWorkAnswerBean>>

    // 获取教师评论(老师端)
    @GET("api/work/getTeacherWorkCommentList")
    fun getHomeWorkComment(
        @Query("studentId") studentId: Int,
        @Query("teacherId") teacherId: Int,
        @Query("workId") workId: Int
    ): Observable<BaseResponse<MutableList<TeacherCommentParentBean>?>>

    // 获取常用评语
    @GET("api/work/getWorkComments")
    fun getCommonComment(
        @Query("teacherId") teacherId: Int
    ): Observable<BaseResponse<MutableList<CommonComment>?>>

    // 提交教师评语
    @POST("api/work/createTeacherWorkComment")
    fun createTeacherComment(@Body req: CreateTeacherCommentReq): Observable<BaseResponse<String>>

    // 创建评语
    @POST("api/work/createWorkComment")
    fun createComment(@Body req: CreateCommentReq): Observable<BaseResponse<String>>
}