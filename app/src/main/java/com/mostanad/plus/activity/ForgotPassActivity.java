package com.mostanad.plus.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;
import okhttp3.ResponseBody;

import static com.mostanad.plus.utils.Constants.APP_NAME_FA;
import static com.mostanad.plus.utils.Constants.IP;
import static com.mostanad.plus.utils.Constants.SPECIAL_MSG;

public class ForgotPassActivity extends AppCompatActivity {


    @BindView(R.id.txt_mobmsg)
    TextView txtMobmsg;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.txt_signup)
    TextView txtSignup;
    @BindView(R.id.txt_message)
    TextView txtMessage;
    @BindView(R.id.txt_version)
    TextView txtVersion;
    private SharedPreferences personInfo;
    private SharedPreferences.Editor editor;

    private String imei;
    private RestHelper restHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);
        restHelper = new RestHelper();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        Log.e("IMEI OF DEVICE", imei);

        personInfo = getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();

        restHelper.GetIp(new RestCallBack.ResponseGetIpFinishListener() {
            @Override
            public void onFinish(ResponseBody response) {
                try {
                    editor.putString(IP, response.string());
                    editor.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {

            }
        });

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");

        inputLayoutPhone.setHintEnabled(false);

        String version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!Utilities.IsAutoDetect()) {
            if (!Utilities.IsWebSiteVersion()) {
                String appVersion = getString(R.string.navVersion) + version;
                txtVersion.setText(appVersion);
            } else {
                String appVersion = getString(R.string.wnavVersion) + version;
                txtVersion.setText(appVersion);
            }
        } else {
            String appVersion = getString(R.string.avVersion) + version;
            txtVersion.setText(appVersion);
        }

        if (Utilities.IsAutoDetect() || !Utilities.IsWebSiteVersion()) {
            txtMessage.setVisibility(View.GONE);
        } else {
            txtMessage.setText(personInfo.getString(SPECIAL_MSG, ""));
        }

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        inputLayoutPhone.setTypeface(typeface);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {

        if (!validatePhone()) {
            return;
        }
        register();
    }

    private boolean validatePhone() {
        if (edtPhone.getText().toString().trim().isEmpty() || !edtPhone.getText().toString().trim().startsWith("09")) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    private void register() {
        final ProgressDialog pd = new ProgressDialog(ForgotPassActivity.this);
        pd.setMessage("در حال ارسال اطلاعات");
        pd.setCancelable(false);
        pd.show();
        String mob_ip = personInfo.getString(IP, "").trim();
        restHelper.Register(editor,edtPhone.getText().toString(), mob_ip, imei, personInfo.getString(Constants.PLAYER_ID, ""), Utilities.GetSensub(), new RestCallBack.ResponseFinishListener() {
            @Override
            public void onFinish(String code) {
                switch (code) {
                    case "200": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "کد فعالسازی برای شما ارسال شد", Toast.LENGTH_SHORT).show();
                        editor.putString(Constants.PHONE_NUMBER, edtPhone.getText().toString());
                        editor.apply();
                        Intent intent = new Intent(ForgotPassActivity.this, ActivateActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                    }
                    case "202": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "کد فعالسازی مجددا ارسال شد", Toast.LENGTH_SHORT).show();
                        editor.putString(Constants.PHONE_NUMBER, edtPhone.getText().toString());
                        editor.apply();
                        Intent intent = new Intent(ForgotPassActivity.this, ActivateActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {
                switch (error) {
                    case "302": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "به "+APP_NAME_FA+" خوش آمدید", Toast.LENGTH_SHORT).show();
                        editor.putString(Constants.PHONE_NUMBER, edtPhone.getText().toString());
                        editor.putBoolean(Constants.IS_ACTIVE, true);
                        editor.apply();
                        Intent intent = new Intent(ForgotPassActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                    }
                    case "404": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "شماره تلفن وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "400": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "شماره تلفن وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "406": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "کاربر قادر به ثبت نام نیست", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "401": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "خطا در برقراری ارتباط", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "خطا در برقراری ارتباط", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}