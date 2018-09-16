package com.mostanad.plus.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import com.mostanad.plus.R;
import com.mostanad.plus.helper.HashGeneratorUtils;
import com.mostanad.plus.interfaces.ApiInterface;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mostanad.plus.utils.Constants.HOST_URL;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Constants.SPECIAL_MSG;

public class RegisterSpecialAlertDialogView extends LinearLayout {


    private View baseView;

    private Button btnGetCode, btnCancel;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;
    private TextView tvMessage;
    private SharedPreferences personInfo;
    private String mobileNumber;


    private String apiKey;
    private String sign;

    public RegisterSpecialAlertDialogView(Context context) {
        super(context);
        initialize(context);

    }

    public RegisterSpecialAlertDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);

    }

    public RegisterSpecialAlertDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }


    public void initialize(Context context) {
        initializeView(context);
        addView(baseView);
        addActions();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog);
        alertDialog = builder.setCancelable(false)
                .setView(this)
                .create();


        personInfo = context.getApplicationContext().getSharedPreferences(PERSON_INFO, Context.MODE_PRIVATE);
        String specialMessage = personInfo.getString(SPECIAL_MSG, "");
        tvMessage.setText(specialMessage);
    }


    private void initializeView(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.view_special_register, this, false);
        btnGetCode = baseView.findViewById(R.id.btn_special_register_ok);
        btnCancel = baseView.findViewById(R.id.btn_special_register_cancel);
        progressBar = baseView.findViewById(R.id.progressBar_view_special_register);
        tvMessage = baseView.findViewById(R.id.tv_content_register_special);
    }

    public void show() {
        alertDialog.show();
    }


    private void addActions() {
        btnGetCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnCancel.setEnabled(false);
                register();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ((Activity) getContext()).finish();
            }
        });
    }

    private String nonce;

    private void register() {


        sign = SignGeneration();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST_URL)
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("user")
                .addPathSegment("special")
                .addQueryParameter("phone", mobileNumber)
                .addQueryParameter("sensub", Utilities.GetSensub())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://google.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface mInterface = retrofit.create(ApiInterface.class);

        Call<JsonElement> registerSpecialCall = mInterface.special(nonce, sign, apiKey, url);
        registerSpecialCall.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if (response != null && response.body() != null) {
                    ActivationSpecialAlertDialogView activationSpecialAlertDialogView;
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject.has("code")) {
                        switch (jsonObject.get("code").getAsInt()) {
                            case 202:
                                activationSpecialAlertDialogView = new ActivationSpecialAlertDialogView(getContext());
                                activationSpecialAlertDialogView.show();
                                alertDialog.dismiss();
                                break;
                            case 200:
                                activationSpecialAlertDialogView = new ActivationSpecialAlertDialogView(getContext());
                                activationSpecialAlertDialogView.show();
                                alertDialog.dismiss();
                                break;
                            case 400:
                                Toast.makeText(getContext(), "خطا در اطلاعات ورودی", Toast.LENGTH_SHORT).show();
                                break;
                            case 404:
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "خطا در اطلاعات ورودی", Toast.LENGTH_SHORT).show();
                                break;
                            case 406:
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "کد فعال سازی وارد شده اشتباه است", Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                }
                progressBar.setVisibility(View.GONE);
                btnCancel.setEnabled(true);

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                btnCancel.setEnabled(true);
            }
        });
    }

    public String randomString(int len) {

        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


    public String SignGeneration() {
        mobileNumber = personInfo.getString(PHONE_NUMBER, "");
        apiKey = personInfo.getString("apiKey", "");
        byte[] data = new byte[0];
        try {
            String burl = Constants.BASE_URL + Constants.RELATIVE_URL_SPECIAL;
            data = burl.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        nonce = randomString(8);
        String s = nonce + Constants.SECRET_KEY + Constants.API_KEY + base64 + "phone=" + mobileNumber + "&sensub=" + Utilities.GetSensub();
        s = s.replace("\n", "").replace("\r", "");

        return HashGeneratorUtils.generateSHA256(s);
    }


    private FinishListener finishListener = new FinishListener() {
        @Override
        public void onFinish() {

        }
    };

    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }

    interface FinishListener {
        void onFinish();
    }
}
