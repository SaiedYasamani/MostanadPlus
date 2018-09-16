package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

public class UserDataModel {
    @SerializedName("id")
    private String id;
    @SerializedName("phone")
    private String phone;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("email")
    private String email;
    @SerializedName("register_time")
    private String register_time;
    @SerializedName("last_activity")
    private String last_activity;
    @SerializedName("status")
    private String status;
    @SerializedName("activation_code")
    private String activation_code;


    public String getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getActivation_code() {
        return activation_code;
    }
}

