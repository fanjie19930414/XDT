package com.kapok.apps.maple.xdt.usercenter.adapter

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kotlin.baselibrary.custom.CustomLessonNumDialog
import com.kotlin.baselibrary.utils.DateUtils

/**
 * 附近学校Adapter
 * fanjie
 */
class SchoolLocationAdapter(val context: Context, dataList: MutableList<NearBySchoolBean>) :
    BaseQuickAdapter<NearBySchoolBean, BaseViewHolder>(R.layout.item_school_location, dataList) {

    override fun convert(helper: BaseViewHolder, item: NearBySchoolBean) {
        helper.getView<TextView>(R.id.tvNearBySchool).text = item.schoolName
    }
}