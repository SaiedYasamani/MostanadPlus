package com.mostanad.plus.pojo;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class HomePageModel {
    @SerializedName("data")
    private JsonObject data;

    public JsonObject getData() {
        return data;
    }

}
