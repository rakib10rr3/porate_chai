package com.rakib.chatappmini_project.model;

public class Chat {

    private String senderId;
    private String text;
    private String name;
    private String photoUrl;
    private String imageUrl;
    private String dateTime;
    private boolean joining;
    private long timestamp;

    public Chat() {
    }

    public Chat(String senderId, String text, String name, String photoUrl, String imageUrl, String dateTime, boolean joining, long timestamp) {
        this.senderId = senderId;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.dateTime = dateTime;
        this.joining = joining;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }



    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isJoining() {
        return joining;
    }

    public void setJoining(boolean joining) {
        this.joining = joining;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
