package com.kapok.apps.maple.xdt.usercenter.adapter

import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R

/**
 *  选择班级Adapter
 *  fanjie
 */
class ChooseGradeAdapter(// 已选中的年级
    private val context: Context, dataList: ArrayList<String>, private val selectGrade: String
) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_choose_grade, dataList) {

    override fun convert(helper: BaseViewHolder, item: String?) {
        if (selectGrade == item) {
            helper.getView<TextView>(R.id.tvGrade)
                .setTextColor(context.resources.getColor(com.kotlin.baselibrary.R.color.login_xdt_btn_color_able))
        } else {
            helper.getView<TextView>(R.id.tvGrade)
                .setTextColor(context.resources.getColor(com.kotlin.baselibrary.R.color.text_xdt))
        }
        helper.getView<TextView>(R.id.tvGrade).text = item
    }
}