package com.kapok.apps.maple.xdt.home.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.MessageFragmentListBean
import com.kapok.apps.maple.xdt.home.bean.MsgDataDB
import com.kapok.apps.maple.xdt.home.bean.MsgDetailDB
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.ex.setVisible

/**
 *  MessageDetailListAdapter
 */
class MessageDetailListAdapter(messageBean: MutableList<MsgDetailDB>) :
    BaseQuickAdapter<MsgDetailDB, BaseViewHolder>(R.layout.item_message_detail_list_item, messageBean) {
    private var more: Boolean = false

    override fun convert(helper: BaseViewHolder?, item: MsgDetailDB) {
        val ivCheck = helper?.getView<ImageView>(R.id.ivMessageDetailIsCheck)
        val tvTime = helper?.getView<TextView>(R.id.tvMessageDetailTime)
        val tvTitle = helper?.getView<TextView>(R.id.tvMessageDetailTitle)
        val tvContent = helper?.getView<TextView>(R.id.tvMessageDetailContent)
        if (more) {
            ivCheck?.setVisible(true)
        } else {
            ivCheck?.setVisible(false)
        }
        // 是否选中
        if (item.isCheck) {
            ivCheck?.setImageResource(R.mipmap.chk_box_on)
        } else {
            ivCheck?.setImageResource(R.mipmap.chk_box_off)
        }
        // 时间
        tvTime?.text = item.messageTime
        // 标题
        tvTitle?.text = item.messageTitle
        // 内容
        tvContent?.text = item.messageContent
    }

    fun setMoreChoose(more: Boolean) {
        this.more = more
        notifyDataSetChanged()
    }

}