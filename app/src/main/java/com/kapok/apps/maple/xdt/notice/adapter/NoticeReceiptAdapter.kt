package com.kapok.apps.maple.xdt.notice.adapter

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.notice.bean.ReceiptItemBean

/**
 * 回执Adapter
 */
class NoticeReceiptAdapter(val context: Context, receiptList: MutableList<ReceiptItemBean>) :
    BaseQuickAdapter<ReceiptItemBean, BaseViewHolder>(R.layout.item_receipt, receiptList) {

    override fun convert(helper: BaseViewHolder, item: ReceiptItemBean) {
        val tvReceiptNum = helper.getView<TextView>(R.id.tvReceiptNum)
        helper.addOnClickListener(R.id.ivReceiptDel)
        val etReceiptContent = helper.getView<EditText>(R.id.etReceiptContent)
        // Num
        tvReceiptNum.text = item.titleNum
        // Content
        if (item.content.isNotEmpty()) etReceiptContent.hint = "请输入选项内容" else etReceiptContent.text = SpannableStringBuilder(item.content)
        // TextWatcher
        etReceiptContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.content = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

    }
}