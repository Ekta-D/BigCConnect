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
    Map<String, Object> users = new HashMap<>();
    String  media;

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

    public Map<String, Object>getUsers() {
        return users;
    }

    public void setUsers(Map<String, Object> users) {
        this.users = users;
    }



}
