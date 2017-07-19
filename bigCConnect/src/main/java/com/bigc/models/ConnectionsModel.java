package com.bigc.models;

/**
 * Created by beesolver on 7/11/2017.
 */

public class ConnectionsModel {

    public ConnectionsModel() {
    }

    public ConnectionsModel(String createdAt, String from, String objectId, Boolean status, String to, String updatedAt) {
        this.createdAt = createdAt;
        this.from = from;
        this.objectId = objectId;
        this.status = status;
        this.to = to;
        this.updatedAt = updatedAt;
    }

    private String createdAt;
    private String from;
    private String objectId;
    private Boolean status;
    private String to;
    private String updatedAt;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
