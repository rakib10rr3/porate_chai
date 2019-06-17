package com.rakib.chatappmini_project.model;

public class User {
    private String name;
    private String email;
    private String photoUrl;
    private boolean typing;

    public User() {}

    public User(String name, String email, String photoUrl, boolean typing) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.typing = typing;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
