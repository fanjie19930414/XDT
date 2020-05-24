package com.kapok.apps.maple.xdt.homework.bean

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.homework.adapter.CheckCommitHomeWorkListAdapter

class CommitHomeWorkClassInfoBean(
    var classId: Int,
    var className: String,
    var grade: String,
    var gradeId: Int,
    var schoolId: Int,
    var schoolName: Int,
    var startYear: Int,
    var studentCount: Int,
    var studentDetails: List<CommitHomeWorkStudentInfoBean>,
    val submitState: Int = 1
): AbstractExpandableItem<CommitHomeWorkStudentInfoBean>(),MultiItemEntity {
    override fun getLevel(): Int {
        return 0
    }

    override fun getItemType(): Int {
        return CheckCommitHomeWorkListAdapter.TYPE_LEVEL_0
    }
}
