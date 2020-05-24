package com.kapok.apps.maple.xdt.usercenter.adapter

import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.bean.SearchCityListBean

/**
 * 搜索城市列表Adapter
 */
class SearchCityListAdapter(context: Context, dataList: MutableList<SearchCityListBean>) :
    BaseQuickAdapter<SearchCityListBean, BaseViewHolder>(R.layout.item_search_city_list, dataList) {

    private val context = context

    override fun convert(helper: BaseViewHolder, item: SearchCityListBean?) {
        helper.getView<TextView>(R.id.tvSearchCityList).text = item?.cityName
    }
}