package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SingleCommentModel {
    @SerializedName("id")
    private String id;
    @SerializedName("parent_id")
    private String parent_id;
    @SerializedName("clip_id")
    private String clip_id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("time")
    private String time;
    @SerializedName("content")
    private String content;
    @SerializedName("status")
    private String status;
    @SerializedName("childs")
    private ArrayList<SingleCommentModel> childs;

    public ArrayList<SingleCommentModel> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<SingleCommentModel> childs) {
        this.childs = childs;
    }

    public String getId() {
        return id;
    }

    public String getClip_id() {
        return clip_id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }
}
