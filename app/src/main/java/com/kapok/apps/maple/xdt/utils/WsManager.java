package com.kapok.apps.maple.xdt.utils;

import android.accounts.AccountManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.kapok.apps.maple.xdt.home.bean.MsgDataDB;
import com.kotlin.baselibrary.commen.BaseApplication;
import com.kotlin.baselibrary.commen.BaseConstant;
import com.kotlin.baselibrary.rx.BaseRxBus;
import com.kotlin.baselibrary.rx.event.EventGetUnReadMessageBean;
import com.kotlin.baselibrary.utils.AppPrefsUtils;
import com.kotlin.baselibrary.utils.NetWorkUtils;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WsManager {
    private static WsManager mInstance;
    private final String TAG = this.getClass().getSimpleName();

    /**
     * WebSocket config
     */
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private WsStatus mStatus;
    private WebSocket ws;
    private WsListener mListener;
    private String url;
    // 未读消息数
    private int unReadCount;
    // 来消息的类型
    private int mode;

    private WsManager() {
    }

    public static WsManager getInstance() {
        if (mInstance == null) {
            synchronized (WsManager.class) {
                if (mInstance == null) {
                    mInstance = new WsManager();
                }
            }
        }
        return mInstance;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                BaseRxBus.Companion.getMBusInstance().post(new EventGetUnReadMessageBean<>(unReadCount,mode,true));
            }
        }
    };


    public void init() {
        try {
            mListener = new WsListener();
            // 注册时目前写死为学生userId 为2
            String configUrl = BaseConstant.baseImUrl + "rocket/api/register?" +
                    "appId=1" +
                    "&userId=" + "2" +
                    "&endType=1" +
                    "&deviceType=" + Build.BRAND +
                    // deviceToken 没定
                    "&deviceToken=" + "123456";
            ws = new WebSocketFactory().createSocket(configUrl, CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(mListener)//添加回调监听
                    .connectAsynchronously();//异步连接
            setStatus(WsStatus.CONNECTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 继承默认的监听空实现WebSocketAdapter,重写我们需要的方法
     * onTextMessage 收到文字信息
     * onConnected 连接成功
     * onConnectError 连接失败
     * onDisconnected 连接关闭
     */
    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            /*
	            "appId": "115bed83bca74d7399465ab62bd05912",
	            "messageId": "6dfa1036681f455e880240bb1d9a5e56",
	            "channelType": 1,
	            "senderId": "3",
	            "senderType": 1,
	            "receiverId": "2",
	            "encrypted": false,
	            "deliveryMode": 2,
	            "messageType": 1,
	            "messageStatus": 1,
	            "messageDirect": 1,
	            "messageTitle": "作业没有完成",
	            "messageBrief": "通知概要文本",
	            "messageContent": "作业没有完成",
	            "messageTime": 1583228113052
	        */
            Log.i(TAG, text);
            if ("ping".equals(text)) {
                return;
            }
            if (text.length() > 0) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(text);
                    MsgDataDB msgDataDB = JSONObject.toJavaObject(jsonObject, MsgDataDB.class);
                    msgDataDB.setMessageReadStatus(1);
                    msgDataDB.save();
                    unReadCount = getUnreadMsgCount(msgDataDB.getDeliveryMode());
                    mode = msgDataDB.getDeliveryMode();
                    handler.sendMessage(handler.obtainMessage(100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.i(TAG, "连接成功");
            setStatus(WsStatus.CONNECT_SUCCESS);
            cancelReconnect();//连接成功的时候取消重连,初始化连接次数
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            super.onConnectError(websocket, exception);
            Log.i(TAG, "连接失败");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接错误的时候调用重连方法
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Log.i(TAG, "断开连接");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接断开的时候调用重连方法
        }
    }


    private void setStatus(WsStatus status) {
        this.mStatus = status;
    }


    private WsStatus getStatus() {
        return mStatus;
    }

    public void disconnect() {
        if (ws != null) {
            ws.disconnect();
        }
    }

    private Handler mHandler = new Handler();

    private int reconnectCount = 0;//重连次数

    private void reconnect() {
        if (!isNetConnect()) {
            reconnectCount = 0;
            return;
        }

        //这里其实应该还有个用户是否登录了的判断 因为当连接成功后我们需要发送用户信息到服务端进行校验
        //由于我们这里是个demo所以省略了
        if (ws != null && !ws.isOpen() &&//当前连接断开了
                getStatus() != WsStatus.CONNECTING) {//不是正在重连状态

            reconnectCount++;
            setStatus(WsStatus.CONNECTING);
            url = BaseConstant.baseImUrl + "rocket/api/register?" +
                    "appId=1" +
                    "&userId=" + AppPrefsUtils.INSTANCE.getInt("userId") +
                    "&endType=1" +
                    "&deviceType=" + Build.BRAND +
                    // 1111111111111111111111111111111111111111111111111111111111111111111111111111111111111
                    "&deviceToken=" + "123456789";
            Log.i(TAG, url);
            //重连最小时间间隔
            long minInterval = 3000;
            long reconnectTime = minInterval;
            if (reconnectCount > 3) {
                Log.i(TAG, url);
                long temp = minInterval * (reconnectCount - 2);
                //重连最大时间间隔
                long maxInterval = 60000;
                reconnectTime = temp > maxInterval ? maxInterval : temp;
            }
            mHandler.postDelayed(mReconnectTask, reconnectTime);
        }
    }


    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            try {
                ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                        .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                        .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                        .addListener(mListener = new WsListener())//添加回调监听
                        .connectAsynchronously();//异步连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 获取指定类型消息未读数
     * 0 为全部  否则为指定
     */
    private int getUnreadMsgCount(int mode) {
        int unreadMsgCount = 0;
        try {
            if (mode == 0) {
                unreadMsgCount = LitePal.where("messageReadStatus = ?", "1").count(MsgDataDB.class);
            } else {
                unreadMsgCount = LitePal.where("deliveryMode = ? and messageReadStatus = ?", String.valueOf(mode), "1").count(MsgDataDB.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unreadMsgCount;
    }

    private void cancelReconnect() {
        reconnectCount = 0;
        mHandler.removeCallbacks(mReconnectTask);
    }

    private boolean isNetConnect() {
        return NetWorkUtils.INSTANCE.isNetWorkAvailable(BaseApplication.Companion.getContext());
    }

    public enum WsStatus {
        CONNECT_SUCCESS,//连接成功
        CONNECT_FAIL,//连接失败
        CONNECTING //正在连接
    }
}
