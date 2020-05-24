package com.kapok.apps.maple.xdt.timetable.model.model_instance

import com.kapok.apps.maple.xdt.classlist.bean.SubjectByTeacherBean
import com.kapok.apps.maple.xdt.classlist.model.model_req.EditSubjectByTeacherReq
import com.kapok.apps.maple.xdt.timetable.bean.*
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import com.kapok.apps.maple.xdt.timetable.model.TimeTableModel
import com.kapok.apps.maple.xdt.timetable.model.model_req.CreateNewClassReq
import com.kapok.apps.maple.xdt.timetable.model.model_req.SaveClassSettingReq
import com.kapok.apps.maple.xdt.timetable.model.model_req.UpDataTimeTableDetailReq
import com.kapok.apps.maple.xdt.timetable.model.model_req.UpdateClassSubjectInputReq
import com.kapok.apps.maple.xdt.timetable.net.TimeTable
import com.kotlin.baselibrary.ex.convert
import com.kotlin.baselibrary.ex.convertNoResult
import com.kotlin.baselibrary.net.RetrofitFactory
import io.reactivex.Observable

class TimeTableChooseSubjectInstance : TimeTableModel {
    override fun upDataTimeTableDetail(
        classId: String,
        timetableDetailList: MutableList<TimeTableSubjectDetailBean>,
        weekScope: String
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .upDataTimeTableDetail(UpDataTimeTableDetailReq(classId, timetableDetailList, weekScope))
            .convertNoResult()
    }

    // 保存 班级设置接口
    override fun saveSubjectSettingInfo(
        amLessonCount: Int,
        beginDate: String,
        classId: Int,
        endDate: String,
        pmLessonCount: Int,
        timeTableName: String,
        timetableConfigDetailList: MutableList<TimeTableSettingDetailSubjectBean>
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .saveTimeTableSettingInfo(
                SaveClassSettingReq(
                    amLessonCount,
                    beginDate,
                    classId,
                    endDate,
                    pmLessonCount,
                    timeTableName,
                    timetableConfigDetailList
                )
            )
            .convertNoResult()
    }

    // 获取 班级设置接口
    override fun getSubjectSettingInfo(classId: Int): Observable<TimeTableSettingInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .getTimeTableSettingInfo(classId)
            .convert()
    }

    // 获取 班级科目列表接口
    override fun getClassSubjectList(classId: Int): Observable<MutableList<ClassChooseSubjectBean>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .getClassSubjectList(classId)
            .convert()
    }

    // 根据班级获取老师列表接口
    override fun getClassTeacherList(classId: Int): Observable<MutableList<TeacherOutPutVOList>?> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .getClassTeacherList(classId)
            .convert()
    }

    // 班级科目编辑接口
    override fun saveClassSubject(
        classId: Int,
        subjectTeacherListBean: MutableList<SubjectTeacherListBean>,
        userId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .saveClassSubject(UpdateClassSubjectInputReq(classId, subjectTeacherListBean, userId))
            .convertNoResult()
    }

    // 创建新科目接口
    override fun createNewSubject(classId: Int, subjectName: String): Observable<Int> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .createNewSubject(CreateNewClassReq(classId, subjectName))
            .convert()
    }

    // 获取课程表接口
    override fun getTimeTableInfo(classid: Int, week: String): Observable<TimeTableInfoBean> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .getTimeTableInfo(classid, week)
            .convert()
    }

    // 编辑老师课程接口（代课教师）
    override fun editSubjectByTeaceher(
        classId: Int,
        subjectList: MutableList<SubjectByTeacherBean>,
        userId: Int
    ): Observable<String> {
        return RetrofitFactory.retrofitFactoryInstance.create(TimeTable::class.java)
            .editSubjectByTeacher(EditSubjectByTeacherReq(classId, subjectList, userId))
            .convertNoResult()
    }

}