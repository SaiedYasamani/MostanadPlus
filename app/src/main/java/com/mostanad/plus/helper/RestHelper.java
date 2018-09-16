package com.mostanad.plus.helper;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;
import com.mostanad.plus.interfaces.ApiInterface;
import com.mostanad.plus.pojo.AddCommentModel;
import com.mostanad.plus.pojo.CategoriesModel;
import com.mostanad.plus.pojo.CommentsModel;
import com.mostanad.plus.pojo.HomePageModel;
import com.mostanad.plus.pojo.SetLikeModel;
import com.mostanad.plus.pojo.SingleCategoryModel;
import com.mostanad.plus.pojo.UserActivateModel;
import com.mostanad.plus.pojo.UserModel;
import com.mostanad.plus.pojo.VersionCheckModel;
import com.mostanad.plus.pojo.VideosModel;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mostanad.plus.utils.Constants.VERSION_CHECK_URL;

public class RestHelper {

    private ApiInterface mInterface;
    private Retrofit retrofit;

    public RestHelper() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mInterface = retrofit.create(ApiInterface.class);
    }

    public void SearchVideo(String type, String term, int page, int perPage, String user_id, String phone
            , final RestCallBack.ResponseSearchFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        params.put("limit", String.valueOf(perPage));
        params.put("offset", String.valueOf(page));
        params.put("type", type);
        params.put("term", term);

        Call<VideosModel> call = mInterface.search(String.valueOf(perPage), String.valueOf(page), type, term,
                nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CLIPS, params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<VideosModel>() {
            @Override
            public void onResponse(Call<VideosModel> call, retrofit2.Response<VideosModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<VideosModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void SearchRelatedVideo(String type, String term, String title, int page, int perPage, String user_id, String phone
            , final RestCallBack.ResponseSearchFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        params.put("limit", String.valueOf(perPage));
        params.put("offset", String.valueOf(page));
        params.put("type", type);
        params.put("term", term);
        params.put("title", title);

        Call<VideosModel> call = mInterface.searchRelatedVideo(String.valueOf(perPage), String.valueOf(page), type, term, title,
                nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CLIPS, params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<VideosModel>() {
            @Override
            public void onResponse(Call<VideosModel> call, retrofit2.Response<VideosModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<VideosModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void NewestVideo(String type, int page, int perPage
            , final RestCallBack.ResponseSearchFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("type", type);
        params.put("limit", String.valueOf(perPage));
        params.put("offset", String.valueOf(page));

        Call<VideosModel> call = mInterface.getClips(type, String.valueOf(perPage), String.valueOf(page),
                nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CLIPS, params, nonce));
        call.enqueue(new Callback<VideosModel>() {
            @Override
            public void onResponse(Call<VideosModel> call, retrofit2.Response<VideosModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<VideosModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
        /*
        new VolleyHelper<>(new TypeToken<String>() {
        })
                .setBaseUrl(Constants.BASE_URL + Constants.RELATIVE_URL_CLIPS)
                .setRequestMethod(GET)
                .addHeader("nonce", nonce)
                .addHeader("apiKey", Constants.API_KEY)
                .addHeader("sign", Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CLIPS, params, nonce))
                .addHeader("user_id", pref.getString("uid", ""))
                .addHeader("phone", pref.getString("mobnum", ""))
                .addQuery("type", type)
                .addQuery("limit", String.valueOf(perPage))
                .setOffSet(page)
                .setOffSetTag(Constants.OFFSET)
                .setResponseListener(new ResponseListener<String>() {
                    @Override
                    public void onResponse(String s, int i, long l) {
                        finishListener.onFinish(getNewestVideoResponse(s));
                    }
                })
                .setErrorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            NetworkResponse response = error.networkResponse;
                            String json = new String(response.data);
                            json = Utilities.trimMessage(json, "message");
                            errorListener.onError(json);
                        }
                    }
                })
                .start();
*/
    }

    public void Categories(String user_id, String phone, final RestCallBack.ResponseCategoryFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        Call<CategoriesModel> call = mInterface.category(nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CATEGORY_GET, params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<CategoriesModel>() {
            @Override
            public void onResponse(Call<CategoriesModel> call, retrofit2.Response<CategoriesModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    ArrayList<SingleCategoryModel> categoryArrayList = new ArrayList<>();
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        if (response.body().getData().get(i).getParent() != null)
                            categoryArrayList.add(response.body().getData().get(i));
                    }
                    finishListener.onFinish(categoryArrayList);
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<CategoriesModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void CategoryClips(int perPage, int page, String type, String category, String user_id, String phone
            , final RestCallBack.ResponseSearchFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        params.put("limit", String.valueOf(perPage));
        params.put("offset", String.valueOf(page));
        params.put("type", type);
        params.put("category", category);


        Call<VideosModel> call = mInterface.categoryClips(String.valueOf(perPage), String.valueOf(page), type, category,
                nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CLIPS, params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<VideosModel>() {
            @Override
            public void onResponse(Call<VideosModel> call, retrofit2.Response<VideosModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<VideosModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void EditUser(String phone, String password, String name, String email, String user_id
            , final RestCallBack.ResponseUserFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("phone", phone);
        params.put("password", password);
        params.put("name", name);
        params.put("email", email);

        Call<UserModel> call = mInterface.editUser(phone, password, name, email, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_PROFILE, params, nonce), user_id, phone);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void Home(String user_id, String phone, final RestCallBack.ResponseHomeFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        Call<HomePageModel> call = mInterface.home(nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_HOME, params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<HomePageModel>() {
            @Override
            public void onResponse(Call<HomePageModel> call, retrofit2.Response<HomePageModel> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<HomePageModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void Visit(String clip_id, String user_id, String phone, final RestCallBack.ResponseVisitFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        Call<JsonElement> call = mInterface.visit(clip_id, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_VISIT.replace("{clip_id}", clip_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void IsLiked(String clip_id, String ip, String imei, String user_id, String phone, final RestCallBack.ResponseFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("user_id", user_id);
        params.put("ip", ip);
        params.put("uniq_id", imei);

        Call<JsonElement> call = mInterface.isLiked(clip_id, user_id, ip, imei, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_SET_LIKE.replace("{clip_id}", clip_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                String json = "";
                String code = String.valueOf(response.code());
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(code);
                } else {
                    errorListener.onError(code);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void SetLiked(String clip_id, String ip, String imei, String user_id, String phone, final RestCallBack.ResponseLikeFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("user_id", user_id);
        params.put("ip", ip);
        params.put("uniq_id", imei);

        Call<SetLikeModel> call = mInterface.setLike(clip_id, user_id, ip, imei, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_SET_LIKE.replace("{clip_id}", clip_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<SetLikeModel>() {
            @Override
            public void onResponse(Call<SetLikeModel> call, retrofit2.Response<SetLikeModel> response) {
                String json = "";
                String code = String.valueOf(response.code());
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(code);
                }
            }

            @Override
            public void onFailure(Call<SetLikeModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void AddComment(String clip_id, String parent_id, String name, String email, String content, String user_id, String phone, final RestCallBack.ResponseAddCommentFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("user_id", user_id);
        params.put("parent_id", parent_id);
        params.put("name", name);
        params.put("email", email);
        params.put("content", content);

        Call<AddCommentModel> call = mInterface.addComment(clip_id, user_id, parent_id, name, email, content, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_COMMENT_ADD.replace("{clip_id}", clip_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<AddCommentModel>() {
            @Override
            public void onResponse(Call<AddCommentModel> call, retrofit2.Response<AddCommentModel> response) {
                String code = String.valueOf(response.code());
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(code);
                }
            }

            @Override
            public void onFailure(Call<AddCommentModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void GetComments(String clip_id, String user_id, String phone, final RestCallBack.ResponseCommentsFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        Call<CommentsModel> call = mInterface.getComments(clip_id, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_COMMENT_GET.replace("{clip_id}", clip_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<CommentsModel>() {
            @Override
            public void onResponse(Call<CommentsModel> call, retrofit2.Response<CommentsModel> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(String.valueOf(code));
                }
            }

            @Override
            public void onFailure(Call<CommentsModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void ReportComment(String comment_id, String user_id, String phone, final RestCallBack.ResponseVisitFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        Call<JsonElement> call = mInterface.reportComment(comment_id, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_COMMENT_REPORT.replace("{comment_id}", comment_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void ReportClip(String clip_id, String report_type, String user_id, String phone, final RestCallBack.ResponseVisitFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("user_id", user_id);
        params.put("report_type", report_type);

        Call<JsonElement> call = mInterface.reportClip(clip_id, user_id, report_type, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_CLIP_REPORT.replace("{clip_id}", clip_id), params, nonce)
                , user_id, phone);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void SignIn(String phone, final RestCallBack.ResponseUserFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("phone", phone);

        Call<UserModel> call = mInterface.signIn(phone, nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_SIGN_IN, params, nonce));
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, retrofit2.Response<UserModel> response) {
                int code = response.code();

                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(String.valueOf(code));
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void GetSpecialMessage(String url, final RestCallBack.ResponseVisitFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        Call<JsonElement> call = mInterface.getSpecialMessage(url, Utilities.GetSensub(), Utilities.GetAds(), Utilities.getVerType());
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void Register(final SharedPreferences.Editor editor, String phone, String ip, String imei, String player_id, String sensub, final RestCallBack.ResponseFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("phone", phone);
        params.put("ip", ip);
        params.put("imei", imei);
        params.put("player_id", player_id);
        params.put("sensub", sensub);

        Call<JsonElement> call = mInterface.register(phone, ip, imei, player_id, sensub, Utilities.GetAds(), Utilities.getAdline(), nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_SIGN_UP, params, nonce));
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                int code = response.code();

                if (response.isSuccessful()) {

                    if (((JsonObject) response.body()).has("detect") && ((JsonObject) response.body()).get("detect") != null)
                        editor.putString(Constants.PREF_REGEX, ((JsonObject) response.body()).get("detect").toString());

                    finishListener.onFinish(String.valueOf(code));
                } else {
                    editor.putString(Constants.PREF_REGEX, "");
                    errorListener.onError(String.valueOf(code));
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("Error", t.toString());
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void Activate(String phone, String code, String ip, String imei, String player_id, String sensub, final RestCallBack.ResponseActivateFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        String nonce = Utilities.randomString(8);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("ip", ip);
        params.put("imei", imei);
        params.put("player_id", player_id);
        params.put("sensub", sensub);

        Call<UserActivateModel> call = mInterface.activate(nonce, Constants.API_KEY, Utilities.signGeneration(Constants.BASE_URL + Constants.RELATIVE_URL_ACTIVATE, params, nonce), phone, code, ip, imei, player_id, sensub, Utilities.GetAds(), Utilities.getAdline());
        call.enqueue(new Callback<UserActivateModel>() {
            @Override
            public void onResponse(Call<UserActivateModel> call, Response<UserActivateModel> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    Log.e("Debug", "response : " + response.body().toString());
                    finishListener.onFinish(response.body(), response.code());
                } else {
                    errorListener.onError(String.valueOf(code));
                }
            }

            @Override
            public void onFailure(Call<UserActivateModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void GetIp(final RestCallBack.ResponseGetIpFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        Call<ResponseBody> call = mInterface.getIp("http://whatismyip.akamai.com/");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String json = "";
                if (response.errorBody() != null) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        json = jObjError.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                } else {
                    errorListener.onError(json);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void CheckVersion(int version, final RestCallBack.ResponseVersionCheckFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        Call<VersionCheckModel> call = mInterface.checkVersion(VERSION_CHECK_URL + "?version=" + version);
        call.enqueue(new Callback<VersionCheckModel>() {
            @Override
            public void onResponse(Call<VersionCheckModel> call, retrofit2.Response<VersionCheckModel> response) {
                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                }
            }

            @Override
            public void onFailure(Call<VersionCheckModel> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }

    public void DownloadFile(String url, final RestCallBack.ResponseGetIpFinishListener finishListener
            , final RestCallBack.ResponseErrorListener errorListener) {

        Call<ResponseBody> call = mInterface.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    finishListener.onFinish(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorListener.onError("خطای شبکه");
            }
        });
    }
}
