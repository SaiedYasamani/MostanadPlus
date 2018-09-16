package com.mostanad.plus.pojo;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class CommentsModel {
    @SerializedName("code")
    private String code;
    @SerializedName("data")
    private JsonObject data;

    public String getCode() {
        return code;
    }

    public JsonObject getData() {
        return data;
    }

}
