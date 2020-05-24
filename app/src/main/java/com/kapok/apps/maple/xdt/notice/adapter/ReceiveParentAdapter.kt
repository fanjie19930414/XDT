package com.kapok.apps.maple.xdt.notice.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.notice.bean.ReceiveBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.utils.GlideUtils
import kotlinx.android.synthetic.main.activity_check_homework_teacher.*

/**
 *  回执Adapter
 */
@SuppressLint("SetTextI18n")
class ReceiveParentAdapter(dataList: MutableList<ReceiveBean>) :
    BaseQuickAdapter<ReceiveBean, BaseViewHolder>(R.layout.item_receive_parent, dataList) {

    override fun convert(helper: BaseViewHolder, item: ReceiveBean) {
        val ivReceive = helper.getView<ImageView>(R.id.ivReceive)
        val tvReceive = helper.getView<TextView>(R.id.tvReceive)
        helper.addOnClickListener(R.id.ivReceive)
        // 选中
        if (item.isChoose) {
            ivReceive.setImageResource(R.mipmap.chk_box_on)
        } else {
            ivReceive.setImageResource(R.mipmap.chk_box_off)
        }
        // 内容
        tvReceive.text = item.receiptContent
    }
}