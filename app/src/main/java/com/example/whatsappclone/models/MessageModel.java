package com.example.whatsappclone.models;

public class MessageModel {

    private String messageId, messageText, userId;
    Long messageTime;

    // constructors
    public MessageModel() {
    }

    public MessageModel(String messageId, String userId, String messageText, Long messageTime) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.userId = userId;
        this.messageTime = messageTime;
    }

    public MessageModel(String messageId, String messageText) {
        this.messageId = messageId;
        this.messageText = messageText;
    }

    // getter and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserId(String key) {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
