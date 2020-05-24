package com.kotlin.baselibrary.custom.adapter

import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.baselibrary.R

open class CustomBottomDialogAdapter(val context: Context, dataList: ArrayList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.dialog_layout_item_default, dataList) {
    private var selectText: String = ""

    fun setSelectText(selectText: String) {
        this.selectText = selectText
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        if (selectText == item) {
            helper.getView<TextView>(R.id.tv_dialog_layout)
                .setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
        } else {
            helper.getView<TextView>(R.id.tv_dialog_layout)
                .setTextColor(context.resources.getColor(R.color.text_xdt_hint))
        }
        helper.getView<TextView>(R.id.tv_dialog_layout).text = item
    }
}