package com.kapok.apps.maple.xdt.usercenter.adapter

import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.bean.searchSchoolListBean.SchoolListBean

/**
 * 搜索学校列表Adapter
 */
class SearchSchoolListAdapter(context: Context, dataSchoolList: MutableList<SchoolListBean>?) :
    BaseQuickAdapter<SchoolListBean, BaseViewHolder>(R.layout.item_search_city_list, dataSchoolList) {

    private val context = context

    override fun convert(helper: BaseViewHolder, item: SchoolListBean?) {
        helper.getView<TextView>(R.id.tvSearchCityList).text = item?.schoolName
    }
}