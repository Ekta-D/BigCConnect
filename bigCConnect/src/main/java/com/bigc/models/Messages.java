package com.bigc.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ENTER on 11-07-2017.
 */
public class Messages {
    String createdAt;
    String message = "";
    String objectId;
    String sender;
    String updatedAt;
    String user1;
    String user2;
    String media;

    public Messages() {
    }

    public Messages(String createdAt, String message, String objectId, String sender, String updatedAt, String user1, String user2, String media) {
        this.createdAt = createdAt;
        this.message = message;
        this.objectId = objectId;
        this.sender = sender;
        this.updatedAt = updatedAt;
        this.user1 = user1;
        this.user2 = user2;
        this.media = media;
    }
//    boolean isRead = false;
//    boolean Isread=false;
//    public boolean isread() {
//        return Isread;
//    }
//
//    public void setIsread(boolean isread) {
//        Isread = isread;
//    }
//
//
//
//    public boolean isRead() {
//        return isRead;
//    }
//
//    public void setisRead(boolean isRead) {
//        this.isRead = isRead;
//    }


    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }


    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
