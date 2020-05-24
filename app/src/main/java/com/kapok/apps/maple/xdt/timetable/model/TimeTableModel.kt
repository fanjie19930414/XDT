package com.kapok.apps.maple.xdt.timetable.model

import com.kapok.apps.maple.xdt.classlist.bean.SubjectByTeacherBean
import com.kapok.apps.maple.xdt.timetable.bean.*
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.TeacherOutPutVOList
import io.reactivex.Observable

interface TimeTableModel {
    // 提交 课程表设置接口
    fun saveSubjectSettingInfo(
        amLessonCount: Int,
        beginDate: String,
        classId: Int,
        endDate: String,
        pmLessonCount: Int,
        timeTableName: String,
        timetableConfigDetailList: MutableList<TimeTableSettingDetailSubjectBean>
    ): Observable<String>

    // 获取 课程表设置接口
    fun getSubjectSettingInfo(classId: Int): Observable<TimeTableSettingInfoBean>

    // 获取 班级科目列表接口
    fun getClassSubjectList(classId: Int): Observable<MutableList<ClassChooseSubjectBean>?>

    // 根据班级获取老师列表接口
    fun getClassTeacherList(classId: Int): Observable<MutableList<TeacherOutPutVOList>?>

    // 班级科目编辑接口
    fun saveClassSubject(classId: Int, subjectTeacherListBean: MutableList<SubjectTeacherListBean>,userId : Int): Observable<String>

    // 创建新科目接口
    fun createNewSubject(classId: Int, subjectName: String): Observable<Int>

    // 获取课程表接口
    fun getTimeTableInfo(classid: Int, week: String): Observable<TimeTableInfoBean>

    // 课程表详情编辑接口
    fun upDataTimeTableDetail(
        classId: String,
        timetableDetailList: MutableList<TimeTableSubjectDetailBean>,
        weekScope: String
    ): Observable<String>

    // 编辑老师课程接口（代课教师）
    fun editSubjectByTeaceher(classId: Int, subjectList: MutableList<SubjectByTeacherBean>, userId: Int): Observable<String>
}