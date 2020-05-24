package com.kapok.apps.maple.xdt.home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.presenter.SuggestReportPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.SuggestReportView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_suggest_report.*

/**
 * 意见反馈页
 */
@SuppressLint("SetTextI18n")
class SuggestReportActivity : BaseMVPActivity<SuggestReportPresenter>(), SuggestReportView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_report)
        initView()
        initListener()
    }

    private fun initListener() {
        etSuggest.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                etSuggest.setSelection(etSuggest.length())
                when {
                    s!!.length > 300 -> tvSuggestLength.text =
                        Html.fromHtml("<Font color='#fe5252'>" + s.length + "</Font>" + "/100")
                    s.length in 1..299 -> {
                        tvSuggestLength.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
                        tvSuggestLength.text = s.length.toString() + "/300"
                    }
                    else -> {
                        tvSuggestLength.setTextColor(resources.getColor(R.color.text_xdt_hint))
                        tvSuggestLength.text = s.length.toString() + "/300"
                    }
                }
            }
        })

        suggestHeaderBar.getRightView().setOnClickListener {
            if (TextUtils.isEmpty(etSuggest.text.toString())) {
                ToastUtils.showMsg(this@SuggestReportActivity, "请输入信息")
            } else if (TextUtils.isEmpty(etSuggest.text.toString()) && etSuggest.length() > 300) {
                ToastUtils.showMsg(this@SuggestReportActivity, "最多输入300字")
            } else {
                mPresenter.reportSuggest(etSuggest.text.toString(),AppPrefsUtils.getInt("userId"),true)
            }
        }
    }

    private fun initView() {
        mPresenter = SuggestReportPresenter(this)
        mPresenter.mView = this
        suggestHeaderBar.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        suggestHeaderBar.getRightView().setVisible(true)
        suggestHeaderBar.getRightView().text = "提交"
    }

    // 意见反馈回调
    override fun reportSuggest(msg: String) {
        mPresenter.mView.onDismissDialog()
        ToastUtils.showMsg(this,msg)
        finish()
    }
}