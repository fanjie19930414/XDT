package com.kapok.apps.maple.xdt.notice.bean

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.notice.adapter.NoticeDataStatisticsListAdapter

/**
 * 通知数据统计Bean
 */
data class NoticeDataStatisticsBean(
    val classId: Int?,
    val className: String?,
    val grade: String?,
    val gradeId: Int?,
    val schoolId: Int?,
    val schoolName: String?,
    val startYear: Int?,
    val studentCount: Int?,
    val studentDetails: MutableList<NoticeDataStatisticsStudentBean>?,
    val submitState: Int?
) : AbstractExpandableItem<NoticeDataStatisticsStudentBean>(),MultiItemEntity{
    override fun getLevel(): Int {
        return 0
    }

    override fun getItemType(): Int {
        return NoticeDataStatisticsListAdapter.TYPE_LEVEL_0
    }
}