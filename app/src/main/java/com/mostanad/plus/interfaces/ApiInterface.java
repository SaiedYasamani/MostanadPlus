package com.mostanad.plus.interfaces;

import com.google.gson.JsonElement;
import com.mostanad.plus.pojo.AddCommentModel;
import com.mostanad.plus.pojo.CategoriesModel;
import com.mostanad.plus.pojo.CommentsModel;
import com.mostanad.plus.pojo.HomePageModel;
import com.mostanad.plus.pojo.SetLikeModel;
import com.mostanad.plus.pojo.UserActivateModel;
import com.mostanad.plus.pojo.UserModel;
import com.mostanad.plus.pojo.VersionCheckModel;
import com.mostanad.plus.pojo.VideosModel;
import com.mostanad.plus.utils.Constants;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST
    Call<JsonElement> special(@Header("nonce") String nonce, @Header("sign") String sign, @Header("apiKey") String apiKey, @Url HttpUrl url);

    @GET()
    Call<JsonElement> getMessage(@Url HttpUrl url);

    @GET()
    Call<JsonElement> getSpecialMessage(@Url String url, @Query("sensub") String sensub, @Query("ads") String ads, @Query("ver_type") String verType);

    @POST(Constants.RELATIVE_URL_SIGN_UP)
    Call<JsonElement> register(@Query("phone") String phone, @Query("ip") String ip, @Query("imei") String imei, @Query("player_id") String player_id, @Query("sensub") String sensub, @Query("ads") String ads, @Query("adline") String adline,
                               @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign);

    @POST(Constants.RELATIVE_URL_ACTIVATE)
    Call<UserActivateModel> activate(@Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign,
                                     @Query("phone") String phone, @Query("code") String code, @Query("ip") String ip,
                                     @Query("imei") String imei, @Query("player_id") String player_id, @Query("sensub") String sensub,
                                     @Query("ads") String ads, @Query("adline") String adline);

    @GET(Constants.RELATIVE_URL_CLIPS)
    Call<VideosModel> search(@Query("limit") String limit, @Query("offset") String offset, @Query("type") String type, @Query("term") String term,
                             @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @GET(Constants.RELATIVE_URL_CLIPS)
    Call<VideosModel> searchRelatedVideo(@Query("limit") String limit, @Query("offset") String offset, @Query("type") String type, @Query("term") String term, @Query("title") String title,
                                         @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @GET(Constants.RELATIVE_URL_CLIPS)
    Call<VideosModel> getClips(@Query("type") String type, @Query("limit") String limit, @Query("offset") String offset,
                               @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign);

    @GET(Constants.RELATIVE_URL_CATEGORY_GET)
    Call<CategoriesModel> category(@Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @GET(Constants.RELATIVE_URL_CLIPS)
    Call<VideosModel> categoryClips(@Query("limit") String limit, @Query("offset") String offset, @Query("type") String type, @Query("category") String category,
                                    @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @GET(Constants.RELATIVE_URL_HOME)
    Call<HomePageModel> home(@Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @PUT(Constants.RELATIVE_URL_VISIT)
    Call<JsonElement> visit(@Path("clip_id") String clip_id, @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @GET(Constants.RELATIVE_URL_SET_LIKE)
    Call<JsonElement> isLiked(@Path("clip_id") String clip_id, @Query("user_id") String user_id2, @Query("ip") String ip, @Query("uniq_id") String uniq_id,
                              @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @POST(Constants.RELATIVE_URL_SET_LIKE)
    Call<SetLikeModel> setLike(@Path("clip_id") String clip_id, @Query("user_id") String user_id2, @Query("ip") String ip, @Query("uniq_id") String uniq_id,
                               @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @POST(Constants.RELATIVE_URL_COMMENT_ADD)
    Call<AddCommentModel> addComment(@Path("clip_id") String clip_id, @Query("user_id") String user_id2, @Query("parent_id") String parent_id, @Query("name") String name, @Query("email") String email, @Query("content") String content,
                                     @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @GET(Constants.RELATIVE_URL_COMMENT_GET)
    Call<CommentsModel> getComments(@Path("clip_id") String clip_id, @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @PUT(Constants.RELATIVE_URL_COMMENT_REPORT)
    Call<JsonElement> reportComment(@Path("comment_id") String comment_id, @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @POST(Constants.RELATIVE_URL_CLIP_REPORT)
    Call<JsonElement> reportClip(@Path("clip_id") String clip_id, @Query("user_id") String user_id2, @Query("report_type") String report_type,
                                 @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign
            , @Header("user_id") String user_id, @Header("phone") String phone);

    @POST(Constants.RELATIVE_URL_SIGN_IN)
    Call<UserModel> signIn(@Query("phone") String phone,
                           @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign);

    @POST(Constants.RELATIVE_URL_PROFILE)
    Call<UserModel> editUser(@Query("phone") String phone, @Query("password") String password, @Query("name") String name, @Query("email") String email,
                             @Header("nonce") String nonce, @Header("apiKey") String apiKey, @Header("sign") String sign,
                             @Header("user_id") String user_id, @Header("phone") String phone2);

    @Streaming
    @GET()
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET()
    Call<VersionCheckModel> checkVersion(@Url String url);

    @GET()
    Call<ResponseBody> getIp(@Url String url);
}
