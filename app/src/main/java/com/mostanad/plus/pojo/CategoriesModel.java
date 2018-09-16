package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategoriesModel {
    @SerializedName("data")
    private ArrayList<SingleCategoryModel> data;

    public ArrayList<SingleCategoryModel> getData() {
        return data;
    }

    public CategoriesModel() {
    }
}
