package com.example.whatsappclone.models;

public class StarredMessageModel {
    private String id, senderName, receiverName,  senderProfilePicture, messageText, messageId;
    private Long messageTime;

    // constructors
    public StarredMessageModel() {
    }

    public StarredMessageModel(String id, String senderName, String receiverName, String senderProfilePicture, String messageText, String messageId, Long messageTime) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderProfilePicture = senderProfilePicture;
        this.messageText = messageText;
        this.messageId = messageId;
        this.messageTime = messageTime;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public String getId(String key) {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderProfilePicture() {
        return senderProfilePicture;
    }

    public void setSenderProfilePicture(String senderProfilePicture) {
        this.senderProfilePicture = senderProfilePicture;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
