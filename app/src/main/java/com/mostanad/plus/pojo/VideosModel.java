package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

public class VideosModel {
    @SerializedName("data")
    private LinkedList<SingleVideoModel> data;

    public LinkedList<SingleVideoModel> getData() {
        return data;
    }

    public VideosModel() {
    }
}
