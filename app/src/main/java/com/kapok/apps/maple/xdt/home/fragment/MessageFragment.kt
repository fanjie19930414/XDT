package com.kapok.apps.maple.xdt.home.fragment

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.home.activity.MessageDetailActivity
import com.kapok.apps.maple.xdt.home.adapter.MessageListAdapter
import com.kapok.apps.maple.xdt.home.bean.MessageFragmentListBean
import com.kapok.apps.maple.xdt.home.bean.MsgDataDB
import com.kapok.apps.maple.xdt.home.presenter.MessagePresenter
import com.kapok.apps.maple.xdt.home.presenter.view.MessageView
import com.kotlin.baselibrary.fragment.BaseMvpFragment
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventGetUnReadMessageBean
import com.ycbjie.notificationlib.NotificationUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_message.*
import org.litepal.LitePal
import android.view.MotionEvent
import android.view.Gravity
import android.view.WindowManager
import android.graphics.PixelFormat
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.activity.MainActivity
import com.kotlin.baselibrary.custom.CancelConfirmDialog


/**
 * 消息Fragment
 */
@SuppressLint("SetTextI18n")
class MessageFragment : BaseMvpFragment<MessagePresenter>(), MessageView {
    // 消息实体类
    private lateinit var messageBean: MutableList<MessageFragmentListBean>
    private lateinit var messageAdapter: MessageListAdapter
    // 未读消息数
    private lateinit var disposableIdentity: Disposable
    private var unReadMessageCount: Int = 0
    // 新来消息的类型
    private var newMessageMode: Int = 0
    // 是否需要弹出提醒弹框
    private var isNotify: Boolean = true
    // 悬浮窗
    private lateinit var customView: View
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var time: String
    // WindowManager TAG
    private lateinit var windowManager: WindowManager
    // 3s自动关闭跳转首页
    private val mHandler: Handler = Handler()
    // 多选弹窗
    private lateinit var pop: PopupWindow
    private lateinit var parentView: View
    // 长按选中的Position
    private var selectPosition: Int = 0
    // 是否显示弹窗
    private var hasView = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mPresenter = MessagePresenter(context!!)
        mPresenter.mView = this
        // 获取全部未读信息数量并展示
        if (getUnreadMsgCount(0) > 0) {
            tvNoticeListTeacher.text = "消息(" + getUnreadMsgCount(0) + ")"
        } else {
            tvNoticeListTeacher.text = "消息"
        }
        // 配置Rv (填充固定4个类型 1通知 2作业 3课程表 4动态)
        // 应该先从数据库读取一下各个类型的历史存储数据
        messageBean = arrayListOf()
        for (i in 1..4) {
            var messageFragmentListBean: MessageFragmentListBean
            val msgDB =
                LitePal.where("deliveryMode = ?", i.toString()).findLast(MsgDataDB::class.java)
            val unReadMessageCount = getUnreadMsgCount(i)
            messageFragmentListBean = if (msgDB == null) {
                MessageFragmentListBean(i, "", "", "", "", 0)
            } else {
                MessageFragmentListBean(
                    i,
                    msgDB.messageContent,
                    msgDB.messageTime,
                    msgDB.messageContent,
                    msgDB.messageBrief,
                    unReadMessageCount
                )
            }
            messageBean.add(messageFragmentListBean)
        }
        messageAdapter = MessageListAdapter(messageBean)
        rvMessageList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvMessageList.adapter = messageAdapter
        // 悬浮窗View
        windowManager = context?.getSystemService(WINDOW_SERVICE) as WindowManager
        customView = LayoutInflater.from(context).inflate(R.layout.item_message_notice, null)
        // Pop
        initPop()
    }

    private fun initPop() {
        pop = PopupWindow(activity)
        parentView =
            LayoutInflater.from(context).inflate(R.layout.layout_item_message_has_read, null)
        pop.contentView = parentView
        pop.height = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.width = ViewGroup.LayoutParams.WRAP_CONTENT
        pop.isTouchable = true
        pop.isFocusable = true
        pop.setBackgroundDrawable(resources.getDrawable(R.color.transparent))
        pop.isOutsideTouchable = true
        pop.update()
        pop.setOnDismissListener {
            val lp = activity!!.window.attributes
            lp.alpha = 1f
            activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            activity!!.window.attributes = lp
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initData() {
        // 订阅事件
        disposableIdentity =
            BaseRxBus.mBusInstance.toObservable(EventGetUnReadMessageBean::class.java)
                .subscribe {
                    // 数据更新
                    // 获取全部未读信息数量并展示
                    if (getUnreadMsgCount(0) > 0) {
                        tvNoticeListTeacher.text = "消息(" + getUnreadMsgCount(0) + ")"
                    } else {
                        tvNoticeListTeacher.text = "消息"
                    }
                    unReadMessageCount = it.data as Int
                    newMessageMode = it.type
                    isNotify = it.notify
                    val msgDB = LitePal.where("deliveryMode = ?", newMessageMode.toString())
                        .findLast(MsgDataDB::class.java)
                    if (msgDB != null) {
                        val unReadMessageCount = getUnreadMsgCount(newMessageMode)
                        messageBean[newMessageMode - 1] = MessageFragmentListBean(
                            newMessageMode,
                            msgDB.messageContent,
                            msgDB.messageTime,
                            msgDB.messageContent,
                            msgDB.messageBrief,
                            unReadMessageCount
                        )
                        messageAdapter.notifyDataSetChanged()
                        // 通知栏
                        if (isNotify) {
                            title = msgDB.messageTitle
                            content = msgDB.messageContent
                            time = msgDB.messageTime
                            val resultIntent = Intent(context, MessageDetailActivity::class.java)
                            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            resultIntent.putExtra("type",newMessageMode)
                            val resultPendingIntent = PendingIntent.getActivity(context,3,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                            val notifyCationUtil = NotificationUtils(context)
                            notifyCationUtil
                                //让通知左右滑的时候是否可以取消通知
                                .setOngoing(true)
                                //设置内容点击处理intent
                                .setContentIntent(resultPendingIntent)
                                //设置状态栏的标题
                                .setTicker(msgDB.messageTitle)
                                //必须设置的属性，发送通知
                                .sendNotification(3,msgDB.messageTitle, msgDB.messageContent, R.mipmap.xdt_logo)
                            // 悬浮窗
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!Settings.canDrawOverlays(context)) {
                                    startActivityForResult(
                                        Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + context?.packageName)
                                        ), 100
                                    )
                                } else {
                                    if (!hasView) {
                                        initWindowManager(
                                            newMessageMode,
                                            msgDB.messageTitle,
                                            msgDB.messageContent,
                                            msgDB.messageTime
                                        )
                                    }
                                }
                            } else {
                                if (!hasView) {
                                    initWindowManager(
                                        newMessageMode,
                                        msgDB.messageTitle,
                                        msgDB.messageContent,
                                        msgDB.messageTime
                                    )
                                }
                            }
                        }
                    } else {
                        messageBean[newMessageMode - 1] =
                            MessageFragmentListBean(newMessageMode, "", "", "", "", 0)
                        messageAdapter.notifyDataSetChanged()
                    }
                }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        // 清空所有消息为已读
        ivCleanMessage.setOnClickListener {
            val confirmDialog = CancelConfirmDialog(
                context!!, R.style.BottomDialog
                , "将全部消息置为已读？", ""
            )
            confirmDialog.show()
            confirmDialog.setOnClickConfirmListener(object :
                CancelConfirmDialog.ClickConfirmListener {
                override fun confirm() {
                    clearUnreadMsg(0)
                    for (item in messageBean) {
                        item.unReadMessageCount = 0
                    }
                    messageAdapter.notifyDataSetChanged()
                    // 获取全部未读信息数量并展示
                    if (getUnreadMsgCount(0) > 0) {
                        tvNoticeListTeacher.text = "消息(" + getUnreadMsgCount(0) + ")"
                    } else {
                        tvNoticeListTeacher.text = "消息"
                    }
                    confirmDialog.dismiss()
                }
            })
        }
        // 进入消息详情
        messageAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                // 清空当前未读状态
                clearUnreadMsg(messageBean[position].deliveryMode)
                val intent = Intent(context, MessageDetailActivity::class.java)
                intent.putExtra("type", messageBean[position].deliveryMode)
                startActivity(intent)
            }
        // 长按Item标记为已读
        messageAdapter.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { _, view, position ->
                selectPosition = position
                showPopUpWindow(view)
                true
            }
        // 单挑标记已读
        parentView.findViewById<TextView>(R.id.tvMessageHasRead)
            .setOnClickListener {
                // 标记为已读 (从数据库删除此条选中数据 并刷新)
                clearUnreadMsg(messageBean[selectPosition].deliveryMode)
                for (item in messageBean) {
                    if (item.deliveryMode == messageBean[selectPosition].deliveryMode) {
                        item.unReadMessageCount = 0
                    }
                }
                messageAdapter.notifyDataSetChanged()
                // 获取全部未读信息数量并展示
                if (getUnreadMsgCount(0) > 0) {
                    tvNoticeListTeacher.text = "消息(" + getUnreadMsgCount(0) + ")"
                } else {
                    tvNoticeListTeacher.text = "消息"
                }
                pop.dismiss()
            }
        // 设置悬浮窗的Touch监听
        customView.findViewById<RelativeLayout>(R.id.rlMessageNotice)
            .setOnTouchListener { _, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 停止3s倒计时
                        mHandler.removeCallbacksAndMessages(null)
                        hasView = false
                        windowManager.removeView(customView)
                        val windowColor = activity?.window?.attributes
                        windowColor?.alpha = 1f
                        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        activity?.window?.attributes = windowColor
                        // 来消息跳转详情
                        val intent = Intent(context, MessageDetailActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("type", newMessageMode)
                        startActivity(intent)
                    }
                }
                true
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseRxBus.mBusInstance.unSubscribe(disposableIdentity)
    }

    /**
     *  进入详情后将未读状态清空
     */
    private fun clearUnreadMsg(mode: Int) {
        try {
            val updateValues = ContentValues()
            updateValues.put("messageReadStatus", 0)
            if (mode == 0) {
                // 全置为未读
                LitePal.updateAll(MsgDataDB::class.java, updateValues)
            } else {
                // mode项目置为已读
                LitePal.updateAll(
                    MsgDataDB::class.java,
                    updateValues,
                    "deliveryMode = ?",
                    mode.toString()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取指定类型消息未读数
     * 0 为全部  否则为指定
     */
    private fun getUnreadMsgCount(mode: Int): Int {
        var unreadMsgCount = 0
        try {
            unreadMsgCount = if (mode == 0) {
                LitePal.where("messageReadStatus = ?", "1").count(MsgDataDB::class.java)
            } else {
                LitePal.where(
                    "deliveryMode = ? and messageReadStatus = ?",
                    mode.toString(),
                    "1"
                ).count(
                    MsgDataDB::class.java
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return unreadMsgCount
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initWindowManager(type: Int, title: String, content: String, time: String) {
        val params = WindowManager.LayoutParams()
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        /**
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
         * 即拉下通知栏不可见
         */
        val lp = activity?.window?.attributes
        lp?.alpha = 0.6f
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity?.window?.attributes = lp
        // 设置Window flag
        params.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        /**
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
         * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
         * 设置悬浮窗的长得宽
         */
        params.format = PixelFormat.RGBA_8888
        params.width = windowManager.defaultDisplay.width - 40
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.x = 20
        params.y = 40
        params.gravity = Gravity.START or Gravity.TOP
        // 初始化自定义悬浮窗
        val tvMessageTitleNotice = customView.findViewById<TextView>(R.id.tvMessageTitleNotice)
        val tvMessageDescNotice = customView.findViewById<TextView>(R.id.tvMessageDescNotice)
        val tvMessageTimeNotice = customView.findViewById<TextView>(R.id.tvMessageTimeNotice)
        // 填充数据
        tvMessageTitleNotice.text = title
        tvMessageDescNotice.text = content
        tvMessageTimeNotice.text = time
        hasView = true
        windowManager.addView(customView, params)
        // 3s后消失悬浮窗
        var mCount = 3
        val countDown = object : Runnable {
            override fun run() {
                if (mCount > 0) {
                    hasView = true
                    mHandler.postDelayed(this, 1000)
                } else {
                    hasView = false
                    windowManager.removeView(customView)
                    val windowColor = activity?.window?.attributes
                    windowColor?.alpha = 1f
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    activity?.window?.attributes = windowColor
                }
                mCount--
            }
        }
        mHandler.postDelayed(countDown, 0)
    }

    private fun showPopUpWindow(v: View?) {
        parentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = parentView.measuredHeight
        val popupWidth = parentView.measuredWidth

        // 产生背景变暗效果
        val lp = activity!!.window.attributes
        lp.alpha = 0.6f
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity!!.window.attributes = lp
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
            location[1] + popupHeight
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (!hasView) {
                initWindowManager(newMessageMode, title, content, time)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler.removeCallbacksAndMessages(null)
    }
}