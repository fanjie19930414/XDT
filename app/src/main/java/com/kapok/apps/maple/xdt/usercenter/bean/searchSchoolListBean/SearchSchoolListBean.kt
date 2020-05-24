package com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean

/**
 * 搜索学校Bean
 * fanjie
 */

data class SearchSchoolListBean(
    val condition: ConditionBean,
    val list: MutableList<SchoolListBean>?,
    val order: String,
    val pageNo: Int,
    val rowCntPerPage: Int,
    val totalPage: Int,
    val totalRowCount: Int
)
