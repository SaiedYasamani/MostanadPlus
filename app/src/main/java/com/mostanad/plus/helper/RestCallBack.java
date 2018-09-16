package com.mostanad.plus.helper;

import com.google.gson.JsonElement;

import com.mostanad.plus.pojo.AddCommentModel;
import com.mostanad.plus.pojo.CommentsModel;
import com.mostanad.plus.pojo.HomePageModel;
import com.mostanad.plus.pojo.SetLikeModel;
import com.mostanad.plus.pojo.SingleCategoryModel;
import com.mostanad.plus.pojo.UserActivateModel;
import com.mostanad.plus.pojo.UserModel;
import com.mostanad.plus.pojo.VersionCheckModel;
import com.mostanad.plus.pojo.VideosModel;

import java.util.ArrayList;

import okhttp3.ResponseBody;

public class RestCallBack {
    public interface ResponseErrorListener {
        void onError(String error);
    }

    public interface ResponseFinishListener {
        void onFinish(String error);
    }

    public interface ResponseSearchFinishListener {
        void onFinish(VideosModel singleClips);
    }

    public interface ResponseLikeFinishListener {
        void onFinish(SetLikeModel likeResponse);
    }

    public interface ResponseAddCommentFinishListener {
        void onFinish(AddCommentModel commentResponse);
    }

    public interface ResponseCategoryFinishListener {
        void onFinish(ArrayList<SingleCategoryModel> categories);
    }

    public interface ResponseHomeFinishListener {
        void onFinish(HomePageModel home);
    }

    public interface ResponseCommentsFinishListener {
        void onFinish(CommentsModel home);
    }

    public interface ResponseUserFinishListener {
        void onFinish(UserModel userResponse);
    }

    public interface ResponseVisitFinishListener {
        void onFinish(JsonElement visits);
    }
    public interface ResponseActivateFinishListener{
        void onFinish(UserActivateModel response, int code);
    }

    public interface ResponseGetIpFinishListener{
        void onFinish(ResponseBody response);
    }

    public interface ResponseVersionCheckFinishListener{
        void onFinish(VersionCheckModel response);
    }
}
