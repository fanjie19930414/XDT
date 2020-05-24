package com.kapok.apps.maple.xdt.timetable.net

import com.kapok.apps.maple.xdt.classlist.model.model_req.EditSubjectByTeacherReq
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.TimeTableSettingInfoBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kapok.apps.maple.xdt.timetable.model.model_req.CreateNewClassReq
import com.kapok.apps.maple.xdt.timetable.model.model_req.SaveClassSettingReq
import com.kapok.apps.maple.xdt.timetable.model.model_req.UpDataTimeTableDetailReq
import com.kapok.apps.maple.xdt.timetable.model.model_req.UpdateClassSubjectInputReq
import com.kotlin.baselibrary.net.BaseResponse
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 课程表相关接口
 */
interface TimeTable {
    // 保存 课程表设置接口
    @POST("api/timetable/updatetimetablelessonconfig")
    fun saveTimeTableSettingInfo(@Body req: SaveClassSettingReq): Observable<BaseResponse<String>>

    // 获取 课程表设置接口
    @GET("api/timetable/searchtimetablelessonconfig")
    fun getTimeTableSettingInfo(@Query("classid") classId: Int): Observable<BaseResponse<TimeTableSettingInfoBean>>

    // 获取 班级科目列表接口
    @GET("api/subject/searchclasssubject")
    fun getClassSubjectList(@Query("classid") classId: Int): Observable<BaseResponse<MutableList<ClassChooseSubjectBean>?>>

    // 根据班级获取老师列表接口
    @GET("api/class/searchclassteacher")
    fun getClassTeacherList(@Query("classid") classId: Int): Observable<BaseResponse<MutableList<TeacherOutPutVOList>?>>

    // 班级科目编辑接口
    @POST("api/subject/updateclasssubject")
    fun saveClassSubject(@Body req: UpdateClassSubjectInputReq): Observable<BaseResponse<String>>

    // 创建新课程接口
    @POST("api/subject/createclasssubject")
    fun createNewSubject(@Body req: CreateNewClassReq): Observable<BaseResponse<Int>>

    // 获取课程表接口
    @GET("api/timetable/searchtimetable")
    fun getTimeTableInfo(@Query("classid") classid: Int, @Query("week") week: String): Observable<BaseResponse<TimeTableInfoBean>>

    // 课程表详情编辑接口
    @POST("api/timetable/updatetimetabledetail")
    fun upDataTimeTableDetail(@Body req: UpDataTimeTableDetailReq): Observable<BaseResponse<String>>

    // 编辑老师课程接口（代课教师）
    @POST("api/subject/updateteachersubject")
    fun editSubjectByTeacher(@Body req: EditSubjectByTeacherReq): Observable<BaseResponse<String>>
}