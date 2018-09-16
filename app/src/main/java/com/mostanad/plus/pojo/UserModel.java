package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private UserDataModel data;

    public String getCode() {
        return code;
    }

    public UserDataModel getData() {
        return data;
    }
}
