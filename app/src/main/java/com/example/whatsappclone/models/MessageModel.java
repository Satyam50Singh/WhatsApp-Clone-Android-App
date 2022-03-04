package com.example.whatsappclone.models;

public class MessageModel {

    private String messageId, messageText;
    Long messageTime;

    // constructors
    public MessageModel() {
    }

    public MessageModel(String messageId, String messageText, Long messageTime) {
        this.messageId = messageId;
        this.messageText = messageText;
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

    public String getMessageId(String key) {
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
}
