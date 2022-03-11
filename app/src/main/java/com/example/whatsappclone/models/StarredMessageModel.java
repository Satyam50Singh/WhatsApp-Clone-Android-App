package com.example.whatsappclone.models;

public class StarredMessageModel {
    private String senderName, receiverName,  senderProfilePicture, messageText;
    Long messageTime;

    public StarredMessageModel() {
    }

    public StarredMessageModel(String senderName, String receiverName, Long messageTime, String senderProfilePicture, String messageText) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.messageTime = messageTime;
        this.senderProfilePicture = senderProfilePicture;
        this.messageText = messageText;
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
}
