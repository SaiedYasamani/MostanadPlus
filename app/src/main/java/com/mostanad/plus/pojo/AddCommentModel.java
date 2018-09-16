package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

public class AddCommentModel {
    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private String code;
    @SerializedName("data")
    private SingleCommentModel data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SingleCommentModel getData() {
        return data;
    }

    public void setData(SingleCommentModel data) {
        this.data = data;
    }
}
