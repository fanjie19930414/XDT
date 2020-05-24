package com.kapok.apps.maple.xdt.homework.bean

import android.os.Parcelable
import com.kapok.apps.maple.xdt.notice.bean.TeacherInClassesBean
import kotlinx.android.parcel.Parcelize

/**
 * 老师所在班级Bean
 */
@Parcelize
data class TeacherInClasses(
    val classId: Int,
    val className: String,
    val grade: String,
    val gradeId: Int,
    val startYear: Int,
    var isChoose: Boolean = false,
    var chooseStudentInfo: ArrayList<StudentInClasses> = arrayListOf(),
    var chooseStudentNum: Int = 0,
    var chooseTeacherInfo: ArrayList<TeacherInClassesBean> = arrayListOf(),
    var chooseTeacherNum: Int = 0,
    var schoolId: Int,
    var schoolName: String
) : Parcelable