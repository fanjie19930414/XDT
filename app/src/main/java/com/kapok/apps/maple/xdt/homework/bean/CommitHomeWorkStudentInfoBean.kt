package com.kapok.apps.maple.xdt.homework.bean
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.kapok.apps.maple.xdt.homework.adapter.CheckCommitHomeWorkListAdapter

class CommitHomeWorkStudentInfoBean(
    var classId: Int,
    var commentStatus: Int,
    val studentAvatar: String?,
    var studentId: Int,
    var studentName: String
): MultiItemEntity {
    override fun getItemType(): Int {
        return CheckCommitHomeWorkListAdapter.TYPE_LEVEL_1
    }
}