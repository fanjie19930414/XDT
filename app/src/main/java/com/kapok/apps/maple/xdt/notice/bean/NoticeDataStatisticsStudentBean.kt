package com.kapok.apps.maple.xdt.notice.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.notice.adapter.NoticeDataStatisticsListAdapter

/**
 * 统计数据学生详情Bean
 */
data class NoticeDataStatisticsStudentBean(
    val classId: Int?,
    val commentStatus: Int?,
    val studentAvatar: String?,
    val studentId: Int,
    val studentName: String?
): MultiItemEntity {
    override fun getItemType(): Int {
        return NoticeDataStatisticsListAdapter.TYPE_LEVEL_1
    }
}