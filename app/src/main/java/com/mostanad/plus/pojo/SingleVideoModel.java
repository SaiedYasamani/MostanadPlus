package com.mostanad.plus.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SingleVideoModel implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("category")
    private String category;
    @SerializedName("title_en")
    private String title_en;
    @SerializedName("title_fa")
    private String title_fa;
    @SerializedName("description")
    private String description;
    @SerializedName("content")
    private String content;
    @SerializedName("visit")
    private String visit;
    @SerializedName("like")
    private String like;
    @SerializedName("publish_time")
    private String publish_time;
    @SerializedName("private")
    private String _private;
    @SerializedName("file_url")
    private String file_url;
    @SerializedName("image")
    private String image;
    @SerializedName("featured")
    private boolean featured;
    @SerializedName("in_slider")
    private boolean in_slider;
    @SerializedName("cat_name")
    private String cat_name;

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle_fa() {
        return title_fa;
    }

    public String getDescription() {
        return description;
    }

    public String getVisit() {
        return visit;
    }

    public String getLike() {
        return like;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public String getFile_url() {
        return file_url;
    }

    public String getImage() {
        return image;
    }

    public SingleVideoModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.category);
        dest.writeString(this.title_en);
        dest.writeString(this.title_fa);
        dest.writeString(this.description);
        dest.writeString(this.content);
        dest.writeString(this.visit);
        dest.writeString(this.like);
        dest.writeString(this.publish_time);
        dest.writeString(this._private);
        dest.writeString(this.file_url);
        dest.writeString(this.image);
        dest.writeByte(this.featured ? (byte) 1 : (byte) 0);
        dest.writeByte(this.in_slider ? (byte) 1 : (byte) 0);
        dest.writeString(this.cat_name);
    }

    protected SingleVideoModel(Parcel in) {
        this.id = in.readString();
        this.category = in.readString();
        this.title_en = in.readString();
        this.title_fa = in.readString();
        this.description = in.readString();
        this.content = in.readString();
        this.visit = in.readString();
        this.like = in.readString();
        this.publish_time = in.readString();
        this._private = in.readString();
        this.file_url = in.readString();
        this.image = in.readString();
        this.featured = in.readByte() != 0;
        this.in_slider = in.readByte() != 0;
        this.cat_name = in.readString();
    }

    public static final Parcelable.Creator<SingleVideoModel> CREATOR = new Parcelable.Creator<SingleVideoModel>() {
        @Override
        public SingleVideoModel createFromParcel(Parcel source) {
            return new SingleVideoModel(source);
        }

        @Override
        public SingleVideoModel[] newArray(int size) {
            return new SingleVideoModel[size];
        }
    };
}
