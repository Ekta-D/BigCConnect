package com.android.ex.chips;

/**
 * Created by ENTER on 03-07-2017.
 */
public class Users {
    private transient String email;
    private transient String name;
    private transient int type;
    private transient String profile_picture;
    private transient String name_lowercase;
    private transient int ribbon;
    private transient boolean status;
    private transient String location;
    private transient String cancertype;
    private transient String stage;
    private transient int visibility;
    private transient String deactivated_reason = "";
    private transient boolean deactivated;
    private transient String objectId;
    private transient String message;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private transient String createdAt;

    public String getProblem_type() {
        return problem_type;
    }

    public void setProblem_type(String problem_type) {
        this.problem_type = problem_type;
    }

    private transient String problem_type;

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


    public String getDeactivated_reason() {
        return deactivated_reason;
    }

    public void setDeactivated_reason(String deactivated_reason) {
        this.deactivated_reason = deactivated_reason;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCancertype() {
        return cancertype;
    }

    public void setCancertype(String cancertype) {
        this.cancertype = cancertype;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getRibbon() {
        return ribbon;
    }

    public void setRibbon(int ribbon) {
        this.ribbon = ribbon;
    }


    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getName_lowercase() {
        return name_lowercase;
    }

    public void setName_lowercase(String name_lowercase) {
        this.name_lowercase = name_lowercase;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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


}
