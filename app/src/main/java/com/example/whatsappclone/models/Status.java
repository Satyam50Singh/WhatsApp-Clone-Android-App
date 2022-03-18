package com.example.whatsappclone.models;

public class Status {
    private String imageUrl, caption;
    private long timeStamp;

    // constructor
    public Status() {
    }

    public Status(String imageUrl, String caption, long timeStamp) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.timeStamp = timeStamp;
    }

    public Status(String imageUrl, long lastUpdated) {
        this.imageUrl = imageUrl;
        this.timeStamp = lastUpdated;
    }

    // getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
