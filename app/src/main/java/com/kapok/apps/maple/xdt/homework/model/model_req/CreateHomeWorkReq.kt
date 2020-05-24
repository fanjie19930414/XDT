package com.kapok.apps.maple.xdt.homework.model.model_req

import com.kapok.apps.maple.xdt.homework.bean.HomeWorkStudentDetailBean

data class CreateHomeWorkReq(
    val content: String,
    val deadline: String,
    val images: String,
    val state: Int,
    val studentDetailVoList: MutableList<HomeWorkStudentDetailBean>,
    val subjectId: Int,
    val subjectName: String,
    val teacherId: Int,
    val title: String,
    val videoUri: String,
    val workType: Int
)