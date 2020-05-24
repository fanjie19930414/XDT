package com.kapok.apps.maple.xdt.home.bean;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * 消息通知数据数据库
 */
public class MsgDetailDB implements Serializable {
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
    //  类型 1通知 2作业 3课程表 4动态
    private int deliveryMode;
    private String messageId;
    private String messageType;
    private String messageStatus;
    private String messageDirect;
    private String messageTitle;
    private String messageBrief;
    private String messageContent;
    private String messageTime;
    // 是否选中
    private boolean isCheck = false;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageDirect() {
        return messageDirect;
    }

    public void setMessageDirect(String messageDirect) {
        this.messageDirect = messageDirect;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageBrief() {
        return messageBrief;
    }

    public void setMessageBrief(String messageBrief) {
        this.messageBrief = messageBrief;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
