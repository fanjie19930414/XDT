package com.kapok.apps.maple.xdt.home.presenter.view

import com.kapok.apps.maple.xdt.home.bean.NewsBean
import com.kotlin.baselibrary.presenter.view.BaseView

interface NewsView : BaseView{
    fun getNewsList(newsBean: NewsBean,isFirst: Boolean)
}