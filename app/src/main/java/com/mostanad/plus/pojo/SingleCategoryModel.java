package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

public class SingleCategoryModel {
    @SerializedName("id")
    private String id;
    @SerializedName("name_fa")
    private String name_fa;
    @SerializedName("name_en")
    private String name_en;
    @SerializedName("parent")
    private String parent;
    @SerializedName("image_file")
    private String image_file;
    @SerializedName("list_type")
    private String list_type;
    @SerializedName("priority")
    private String priority;
    @SerializedName("status")
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_fa() {
        return name_fa;
    }

    public String getImage_file() {
        return image_file;
    }

    public String getParent() {
        return parent;
    }

    public SingleCategoryModel() {
    }
}