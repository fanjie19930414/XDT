package com.kapok.apps.maple.xdt.home.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.MessageFragmentListBean
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView
import com.kotlin.baselibrary.ex.setVisible

/**
 *  MessageAdapter
 */
class MessageListAdapter(messageBean: MutableList<MessageFragmentListBean>) :
    BaseQuickAdapter<MessageFragmentListBean, BaseViewHolder>(R.layout.item_message_list_item, messageBean) {

    override fun convert(helper: BaseViewHolder?, item: MessageFragmentListBean?) {
        val messageImg = helper?.getView<CustomRoundAngleImageView>(R.id.civMessageImg)
        val messageTitle = helper?.getView<TextView>(R.id.tvMessageTitle)
        val messageDesc = helper?.getView<TextView>(R.id.tvMessageDesc)
        val messageTime = helper?.getView<TextView>(R.id.tvMessageTime)
        val messageRemind = helper?.getView<TextView>(R.id.tvMessageRemind)
        when (item?.deliveryMode) {
            // 通知
            1 -> {
                messageImg?.setImageResource(R.mipmap.news_icon_sys)
                messageTitle?.text = "通知"
                messageDesc?.text = item.messageTitle
                messageTime?.text = item.messageTime
                if (item.unReadMessageCount > 0) {
                    messageRemind?.setVisible(true)
                    messageRemind?.text = item.unReadMessageCount.toString()
                } else {
                    messageRemind?.setVisible(false)
                }
            }
            // 作业
            2 -> {
                messageImg?.setImageResource(R.mipmap.news_icon_work)
                messageTitle?.text = "作业"
                messageDesc?.text = item.messageTitle
                messageTime?.text = item.messageTime
                if (item.unReadMessageCount > 0) {
                    messageRemind?.setVisible(true)
                    messageRemind?.text = item.unReadMessageCount.toString()
                } else {
                    messageRemind?.setVisible(false)
                }
            }
            // 课程表
            3 -> {
                messageImg?.setImageResource(R.mipmap.news_icon_class)
                messageTitle?.text = "课程表"
                messageDesc?.text = item.messageTitle
                messageTime?.text = item.messageTime
                if (item.unReadMessageCount > 0) {
                    messageRemind?.setVisible(true)
                    messageRemind?.text = item.unReadMessageCount.toString()
                } else {
                    messageRemind?.setVisible(false)
                }
            }
            // 动态
            4 -> {
                messageImg?.setImageResource(R.mipmap.news_icon_mem)
                messageTitle?.text = "动态"
                messageDesc?.text = item.messageTitle
                messageTime?.text = item.messageTime
                if (item.unReadMessageCount > 0) {
                    messageRemind?.setVisible(true)
                    messageRemind?.text = item.unReadMessageCount.toString()
                } else {
                    messageRemind?.setVisible(false)
                }
            }
        }
    }
}