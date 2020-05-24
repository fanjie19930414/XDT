package com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean

/**
 * 班级对应课程列表页
 * fanjie
 */
data class ClassChooseSubjectBean(
    // 用于课程表页面 选中课程使用
    var isChoose : Boolean = false,
    // 是否选中
    var isSelected: Boolean,
    // 类别（0 全局；1 属于某学校；2 属于某个班）
    val ownerType: Int,
    // 科目id
    val subjectId: Int,
    // 科目名称
    val subjectName: String,
    // 科目下的老师id 教师id 多个以,分割
    var teacherids: String,
    // 教师列表
    var teacherOutPutVOList : MutableList<TeacherOutPutVOList>
)