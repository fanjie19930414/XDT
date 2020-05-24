package com.kapok.apps.maple.xdt.home.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.WebViewActivity
import com.kapok.apps.maple.xdt.home.adapter.NewsListAdapter
import com.kapok.apps.maple.xdt.home.bean.NewsBean
import com.kapok.apps.maple.xdt.home.bean.NewsListBean
import com.kapok.apps.maple.xdt.home.presenter.NewsPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.NewsView
import com.kotlin.baselibrary.custom.CustomLoadMoreView
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_found.*

/**
 * 发现Fragment
 */
class FoundFragment : BaseMvpFragment<NewsPresenter>(), NewsView {
    // 新闻列表
    private lateinit var newsListBean: MutableList<NewsListBean>
    // 新闻Adapter
    private lateinit var newsListAdapter: NewsListAdapter
    // 当前页
    private var pageNo: Int = 1
    // 每页记录数
    private var pageSize: Int = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_found, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        getNewsListBean(true)
    }

    private fun initView() {
        mPresenter = NewsPresenter(context!!)
        mPresenter.mView = this
        // 配置Rv
        newsListBean = arrayListOf()
        newsListAdapter = NewsListAdapter(newsListBean)
        rvNewsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        newsListAdapter.setLoadMoreView(CustomLoadMoreView())
        newsListAdapter.setOnLoadMoreListener(
            { getNewsListBean(false) },
            rvNewsList
        )
        rvNewsList.adapter = newsListAdapter
        rvNewsList.addItemDecoration(
            RecycleViewDivider(
                context,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(context!!, 1),
                resources.getColor(R.color.xdt_background)
            )
        )
        // emptyView
        val emptyView = LayoutInflater.from(context)
            .inflate(R.layout.layout_class_list_empty, rvNewsList, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "还没有推荐新闻哦~"
        newsListAdapter.emptyView = emptyView
    }

    private fun getNewsListBean(isFirst: Boolean) {
        pageNo = if (isFirst) {
            1
        } else {
            pageNo + 1
        }
        mPresenter.getNewsList(pageNo, pageSize,isFirst)
    }

    private fun initListener() {
        // 下拉刷新
        refreshNews.setOnRefreshListener { getNewsListBean(true) }
        // WebView
        newsListAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                if (newsListBean[position].link.isNotEmpty()) {
                    val intent = Intent(activity,WebViewActivity::class.java)
                    intent.putExtra("url",newsListBean[position].link)
                    intent.putExtra("title",newsListBean[position].title)
                    startActivity(intent)
                } else {
                    ToastUtils.showMsg(context!!,"链接失效")
                }
            }
    }

    // 获取新闻列表
    override fun getNewsList(newsBean: NewsBean, isFirst: Boolean) {
        if (refreshNews.isRefreshing) {
            refreshNews.isRefreshing = false
        }
        if (newsBean.list != null && newsBean.list.isNotEmpty()) {
            // 列表
            if (isFirst) {
                newsListBean.clear()
                for (item in newsBean.list) {
                    newsListBean.add(item)
                }
                newsListAdapter.setNewData(newsBean.list)
            } else {
                for (item in newsBean.list) {
                    newsListBean.add(item)
                }
                newsListAdapter.addData(newsBean.list)
            }
            newsListAdapter.setEnableLoadMore(newsListAdapter.itemCount - 1 < newsBean.total)
            if (newsListAdapter.itemCount - 1 < newsBean.total) {
                newsListAdapter.loadMoreComplete()
            } else {
                newsListAdapter.loadMoreEnd()
            }
        }
    }
}