package com.mostanad.plus.view;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import com.mostanad.plus.R;
import com.mostanad.plus.helper.HashGeneratorUtils;
import com.mostanad.plus.interfaces.ApiInterface;
import com.mostanad.plus.interfaces.OnNewMessageListener;
import com.mostanad.plus.receiver.SmsListener;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.mostanad.plus.utils.Constants.API_KEY;
import static com.mostanad.plus.utils.Constants.HOST_URL;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Utilities.isNumber;

public class ActivationSpecialAlertDialogView extends LinearLayout {


    private View baseView;
    private Button btnSendCode, btnCancel;
    private AlertDialog alertDialog;
    private EditText edtActiveCode;

    private String sign;
    private String nonce;
    private String mobileNumber;


    private ProgressBar progressBar;
    private SharedPreferences personInfo;

    private SmsListener smsListener;


    public ActivationSpecialAlertDialogView(Context context) {
        super(context);
        initialize(context);

    }

    public ActivationSpecialAlertDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);

    }

    public ActivationSpecialAlertDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }



    public void getPayam() {
        OnNewMessageListener onNewMessageListener = new OnNewMessageListener() {
            @Override
            public void onNewMessageRecived(String messageNumber) {
                if (messageNumber != null && !messageNumber.equals("")) {
                    edtActiveCode.setText(messageNumber);
                    getContext().unregisterReceiver(smsListener);
                    if (isNumber(edtActiveCode.getText().toString())) {
                        submitForm();
                    } else edtActiveCode.setError("کد فعالسازی وارد شده صحیح نیست");
                }
            }
        };
        String regex = personInfo.getString(Constants.PREF_REGEX, "");
        if (regex.equals(""))
            regex = null;
        smsListener = new SmsListener(onNewMessageListener,regex);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getContext().registerReceiver(smsListener, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }
    }

    private void submitForm() {
        if (!validateCode()) {
            return;
        }
        register();
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
        byte[] data = new byte[0];
        try {
            String burl = Constants.BASE_URL + Constants.RELATIVE_URL_SPECIAL;
            data = burl.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        nonce = randomString(8);
        String s = nonce + Constants.SECRET_KEY + Constants.API_KEY + base64 + "phone=" + mobileNumber + "&sensub=" + Utilities.GetSensub() + "&code=" + edtActiveCode.getText().toString();
        s = s.replace("\n", "").replace("\r", "");

        return HashGeneratorUtils.generateSHA256(s);
    }

    private void register() {

        sign = SignGeneration();
        mobileNumber = personInfo.getString(PHONE_NUMBER, "");

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(HOST_URL)
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("user")
                .addPathSegment("special")
                .addQueryParameter("phone", mobileNumber)
                .addQueryParameter("sensub", Utilities.GetSensub())
                .addQueryParameter("code", edtActiveCode.getText().toString())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://google.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface mInterface = retrofit.create(ApiInterface.class);


        Call<JsonElement> registerSpecialCall = mInterface.special(nonce, sign, API_KEY, url);
        registerSpecialCall.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if (response != null && response.body() != null) {
                    SharedPreferences personInfo = getContext().getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject.has("code")) {
                        switch (jsonObject.get("code").getAsInt()) {
                            case 202:
                                getContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE).edit().
                                        putString(Constants.PASSWORD, edtActiveCode.getText().toString()).apply();

                                personInfo.edit().putBoolean(Constants.IS_SPECIAL, false).apply();
                                Toast.makeText(getContext(), "ثبت نام شما کامل شد.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();

                                break;
                            case 200:
                                getContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE).edit().
                                        putString(Constants.PASSWORD, edtActiveCode.getText().toString()).apply();
                                personInfo.edit().putBoolean(Constants.IS_SPECIAL, false).apply();
                                Toast.makeText(getContext(), "ثبت نام شما کامل شد.", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateCode() {
        if (edtActiveCode.getText().toString().trim().isEmpty()) {
            edtActiveCode.setError(getContext().getString(R.string.err_msg_phone2));
            return false;
        }
        return true;
    }

    public void initialize(Context context) {
        initializeView(context);
        addView(baseView);
        addActions();
        personInfo = context.getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);

        if (Utilities.IsAutoDetect())
            getPayam();


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog);
        alertDialog = builder.setCancelable(false)
                .setView(this)
                .create();


    }


    private void initializeView(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.view_special_activation, this, false);
        btnSendCode = baseView.findViewById(R.id.btn_special_active_code_send);
        btnCancel = baseView.findViewById(R.id.btn_special_activate_cancel);
        edtActiveCode = baseView.findViewById(R.id.activationCode);
        progressBar = baseView.findViewById(R.id.progressBar_activity_special_activation);


    }

    public void show() {
        alertDialog.show();
    }


    private void addActions() {

        btnSendCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNumber(edtActiveCode.getText().toString())) {
                    progressBar.setVisibility(View.VISIBLE);
                    register();
                } else edtActiveCode.setError("کد فعالسازی وارد شده صحیح نیست");
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
}
