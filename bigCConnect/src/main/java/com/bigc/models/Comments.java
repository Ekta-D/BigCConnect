package com.bigc.models;

import java.io.Serializable;

/**
 * Created by ENTER on 13-07-2017.
 */
public class Comments implements Serializable{
    String createdAt;
    String message = "";
    String objectId;
    String post = "";
    String updatedAt;
    String user;

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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



}
