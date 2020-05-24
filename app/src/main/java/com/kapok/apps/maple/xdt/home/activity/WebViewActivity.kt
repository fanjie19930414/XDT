package com.kapok.apps.maple.xdt.home.activity

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_webview.*
import android.webkit.*
import com.kotlin.baselibrary.ex.setVisible

/**
 * WebView页
 * */
class WebViewActivity : BaseActivity() {
    private lateinit var title: String
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        initData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    private fun initListener() {
        ivWebViewBack.setOnClickListener {
            if (webView.canGoBack()) run {
                webView.goBack() // goBack()表示返回WebView的上一页面
            } else {
                finish()
            }
        }
    }

    private fun initData() {
        title = intent.getStringExtra("title")
        url = intent.getStringExtra("url")
        // WebView 配置
        val webSetting = webView.settings
        //加载缓存否则网络
        if (Build.VERSION.SDK_INT >= 19) {
            webSetting.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        // 设置WebView属性，能够执行Javascript脚本
        webSetting.javaScriptEnabled = true
        webView.settings.blockNetworkImage = false
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        // 打开本地存储，用于发送和接收消息
        webView.settings.domStorageEnabled = true
        // Set cache size to 8 mb by default. should be more than enough
        webView.settings.setAppCacheMaxSize((1024 * 1024 * 8).toLong())
        // This next one is crazy. It's the DEFAULT location for your app's
        // cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        val appCachePath = applicationContext.cacheDir.absolutePath
        webView.settings.setAppCachePath(appCachePath)
        webView.settings.allowFileAccess = true
        webView.settings.setAppCacheEnabled(true)
        // 设置可以支持缩放
        webSetting.setSupportZoom(true)
        // 自适应屏幕
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.isSaveEnabled = true
        webView.keepScreenOn = true
        // 设置setWebChromeClient对象
        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                tvWebViewTitle.text = title
            }
        }
        //设置此方法可在WebView中打开链接，反之用浏览器打开
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && view != null) {
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        view.loadUrl(url)
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.setVisible(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.settings.blockNetworkImage = false
                progressBar.setVisible(false)
            }
        }
        webView.loadUrl(url)
    }
}