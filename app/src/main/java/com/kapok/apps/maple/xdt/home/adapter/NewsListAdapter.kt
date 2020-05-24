package com.kapok.apps.maple.xdt.home.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.NewsListBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.ex.loadUrl
import com.kotlin.baselibrary.ex.setVisible

/**
 * NewsAdapter
 */
class NewsListAdapter(newsListBean: MutableList<NewsListBean>) :
    BaseQuickAdapter<NewsListBean, BaseViewHolder>(R.layout.item_news_list_item, newsListBean) {

    override fun convert(helper: BaseViewHolder, item: NewsListBean?) {
        val tvNewsTitle = helper.getView<TextView>(R.id.tvNewsTitle)
        val civNewsImg = helper.getView<CustomRoundAngleImageView>(R.id.civNewsImg)
        val tvNewsTime = helper.getView<TextView>(R.id.tvNewsTime)
        if (item?.cover.isNullOrEmpty()) {
            civNewsImg.setVisible(false)
        } else {
            civNewsImg.setVisible(true)
            item?.cover?.let { civNewsImg.loadUrl(it) }
        }
        tvNewsTitle.text = item?.title
        tvNewsTime.text = item?.gmtCreate?.substring(0, item.gmtCreate.length - 3)
    }
}