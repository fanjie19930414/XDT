package com.kapok.apps.maple.xdt.home.activity

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.adapter.MessageDetailListAdapter
import com.kapok.apps.maple.xdt.home.bean.MsgDataDB
import com.kapok.apps.maple.xdt.home.bean.MsgDetailDB
import com.kapok.apps.maple.xdt.home.presenter.MessageDetailPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.MessageDetailView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import kotlinx.android.synthetic.main.activity_message_detail.*
import org.litepal.LitePal
import android.graphics.Point
import android.widget.TextView
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventGetUnReadMessageBean


/**
 * 消息详情页
 */
class MessageDetailActivity: BaseMVPActivity<MessageDetailPresenter>(),MessageDetailView {
    // 消息类型
    private var type: Int = 0
    // 消息数据列表
    private lateinit var messageDetailMessageList: MutableList<MsgDetailDB>
    // adapter
    private lateinit var messageDetailAdapter: MessageDetailListAdapter
    // 多选弹窗
    private lateinit var pop: PopupWindow
    private lateinit var parentView: View
    // 长按选中的Position
    private var selectPosition : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_detail)
        initView()
        initData()
        initListener()
    }

    private fun initView() {
        mPresenter = MessageDetailPresenter(this)
        mPresenter.mView = this
        // 获取传入的类型
        type = intent.getIntExtra("type",0)
        // 配置Rv
        messageDetailMessageList = arrayListOf()
        messageDetailAdapter = MessageDetailListAdapter(messageDetailMessageList)
        rvMessageDetail.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMessageDetail.adapter = messageDetailAdapter
        // emptyView
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_class_list_empty2, rvMessageDetail, false)
        emptyView.findViewById<TextView>(R.id.tvEmptyContent).text = "您当前还没有消息~"
        messageDetailAdapter.emptyView = emptyView
        // Pop
        initPop()
    }

    private fun initPop() {
        pop = PopupWindow(this)
        parentView = LayoutInflater.from(this).inflate(R.layout.layout_item_message_detail, null)
        pop.contentView = parentView
        pop.height = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.width = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.isTouchable = true
        pop.isFocusable = true
        pop.setBackgroundDrawable(resources.getDrawable(R.color.transparent))
        pop.isOutsideTouchable = true
        pop.update()
        pop.setOnDismissListener {
            val lp = window.attributes
            lp.alpha = 1f
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = lp
        }
    }

    private fun initData() {
        // 从数据库拿取数据
        val msgList = LitePal.where("deliveryMode = ?",type.toString()).find(MsgDataDB::class.java)
        messageDetailMessageList.clear()
        if (msgList != null && msgList.size > 0) {
            for (item in msgList) {
                val msgDetailDB = MsgDetailDB()
                msgDetailDB.isCheck = false
                msgDetailDB.messageId = item.messageId
                msgDetailDB.deliveryMode = item.deliveryMode
                msgDetailDB.messageType = item.messageType
                msgDetailDB.messageStatus = item.messageStatus
                msgDetailDB.messageDirect = item.messageDirect
                msgDetailDB.messageTitle = item.messageTitle
                msgDetailDB.messageBrief = item.messageBrief
                msgDetailDB.messageContent = item.messageContent
                msgDetailDB.messageTime = item.messageTime
                messageDetailMessageList.add(msgDetailDB)
            }
            messageDetailAdapter.notifyDataSetChanged()
        } else {
            // 置空状态
            ivMessageDetailBack.setVisible(true)
            tvMessageCancel.setVisible(false)
            rlMessageDel.setVisible(false)
            messageDetailAdapter.notifyDataSetChanged()
        }
    }

    private fun initListener() {
        // 返回
        ivMessageDetailBack.setOnClickListener { finish() }
        // 取消多选状态
        tvMessageCancel.setOnClickListener {
            // 置空所有状态
            if (messageDetailMessageList.size > 0) {
                for (item in messageDetailMessageList) {
                    item.isCheck = false
                }
            }
            messageDetailAdapter.setMoreChoose(false)
            ivMessageDetailBack.setVisible(true)
            tvMessageCancel.setVisible(false)
            rlMessageDel.setVisible(false)
        }
        // 多选状态下的点击事件
        messageDetailAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                messageDetailMessageList[position].isCheck = !messageDetailMessageList[position].isCheck
                adapter?.notifyDataSetChanged()
            }
        // 多选删除
        rlMessageDel.setOnClickListener {
            for (item in messageDetailMessageList) {
                if (item.isCheck) {
                    LitePal.deleteAll(MsgDataDB::class.java,"messageId = ?",item.messageId)
                }
            }
            initData()
            // 发消息 通知Fragment 重新刷新数据库
            BaseRxBus.mBusInstance.post(EventGetUnReadMessageBean(0, type,false))
        }
        // 长按选择删除 或者 多选
        messageDetailAdapter.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { _, view, position ->
                selectPosition = position
                showPopUpWindow(view)
                true
            }
        // 删除 或者 多选 点击事件
        parentView.findViewById<TextView>(R.id.tvMessageDel)
            .setOnClickListener {
                // 删除 (从数据库删除此条选中数据 并刷新)
                LitePal.deleteAll(MsgDataDB::class.java,"messageId = ?",messageDetailMessageList[selectPosition].messageId)
                initData()
                pop.dismiss()
                // 发消息 通知Fragment 重新刷新数据库
                BaseRxBus.mBusInstance.post(EventGetUnReadMessageBean(0, type,false))
            }
        parentView.findViewById<TextView>(R.id.tvMessageChoose)
            .setOnClickListener {
                // 多选
                ivMessageDetailBack.setVisible(false)
                tvMessageCancel.setVisible(true)
                rlMessageDel.setVisible(true)
                // adapter
                messageDetailAdapter.setMoreChoose(true)
                pop.dismiss()
            }
    }

    private fun showPopUpWindow(v: View?) {
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = parentView.measuredHeight
        val popupWidth = parentView.measuredWidth

        // 产生背景变暗效果
        val lp =  window.attributes
        lp.alpha = 0.6f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
        //获取需要在其上方显示的控件的位置信息
        val location = IntArray(2)
        v?.getLocationOnScreen(location)
        val defaultDisplay = windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        val windowX = point.x
        //在控件上方显示
        pop.showAtLocation(
            v,
            Gravity.NO_GRAVITY,
            windowX / 2 - popupWidth / 2,
            location[1] - popupHeight / 2
        )
    }
}