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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mostanad.plus.R;
import com.mostanad.plus.helper.AnalyticHandler;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.utils.Utilities;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

import static com.mostanad.plus.utils.Constants.APP_NAME_FA;
import static com.mostanad.plus.utils.Constants.BTN_TXT;
import static com.mostanad.plus.utils.Constants.IMEI;
import static com.mostanad.plus.utils.Constants.IP;
import static com.mostanad.plus.utils.Constants.IS_ACTIVE;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Constants.PLAYER_ID;
import static com.mostanad.plus.utils.Constants.SPECIAL_MSG;

public class RegisterActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.txt_signin)
    TextView txtSignin;
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
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        edtPhone.setOnEditorActionListener(this);
        restHelper = new RestHelper();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        Log.e("IMEI OF DEVICE", imei);

        personInfo = getApplicationContext().getSharedPreferences(PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();
        editor.putString(IMEI, imei).apply();

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
            String appVersion;
            if (Utilities.IsAdline2())
                appVersion = getString(R.string.adline2Version) + version;
            else appVersion = getString(R.string.avVersion) + version;
            txtVersion.setText(appVersion);
        }


//        if (Utilities.IsAutoDetect() || !Utilities.IsWebSiteVersion()) {
//            txtMessage.setVisibility(View.GONE);
//        } else {
//            txtMessage.setText(personInfo.getString(SPECIAL_MSG, ""));
//        }

//        if (Utilities.IsAutoDetect() && !Utilities.IsWebSiteVersion()) {
        btnSignup.setText(personInfo.getString(BTN_TXT, "عضویت"));
//        } else
        if (!Utilities.IsAutoDetect() && Utilities.IsWebSiteVersion()) {
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(personInfo.getString(SPECIAL_MSG, ""));
        }

        txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, ForgotPassActivity.class);
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
        hideKeyboard();
        if (!validatePhone()) {
            return;
        }
        registerUser();
    }

    private boolean validatePhone() {
        if (edtPhone.getText().toString().trim().isEmpty() || !edtPhone.getText().toString().trim().startsWith("09")) {
            edtPhone.setError(getString(R.string.err_msg_phone));
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    private void registerUser() {
        final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("لطفا منتظر بمانید");
        pd.setCancelable(false);
        pd.show();

        String mob_ip = personInfo.getString(IP, "").trim();

        restHelper.Register(editor, edtPhone.getText().toString(), mob_ip, imei, personInfo.getString(PLAYER_ID, ""), Utilities.GetSensub(), new RestCallBack.ResponseFinishListener() {
            @Override
            public void onFinish(String code) {
                switch (code) {
                    case "200": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "کد فعالسازی برای شما ارسال شد", Toast.LENGTH_SHORT).show();
                        editor.putString(PHONE_NUMBER, edtPhone.getText().toString());
                        editor.apply();
                        Intent intent = new Intent(RegisterActivity.this, ActivateActivity.class);
                        startActivity(intent);
                        if (edtPhone.getText().toString().startsWith("091") || edtPhone.getText().toString().startsWith("099"))
                            AnalyticHandler.logRegister(RegisterActivity.this);
                        finish();
                        break;
                    }
                    case "202": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "کد فعالسازی مجددا ارسال شد", Toast.LENGTH_SHORT).show();
                        editor.putString(PHONE_NUMBER, edtPhone.getText().toString());
                        editor.commit();
                        Intent intent = new Intent(RegisterActivity.this, ActivateActivity.class);
                        startActivity(intent);
                        if (edtPhone.getText().toString().startsWith("091") || edtPhone.getText().toString().startsWith("099"))
                            AnalyticHandler.logRegister(RegisterActivity.this);
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
                        Toast.makeText(getApplicationContext(), "به " + APP_NAME_FA + " خوش آمدید", Toast.LENGTH_SHORT).show();
                        editor.putString(PHONE_NUMBER, edtPhone.getText().toString());
                        editor.putBoolean(IS_ACTIVE, true);
                        editor.apply();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "400": {
                        pd.cancel();
                        Toast.makeText(getApplicationContext(), "شماره تلفن وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "404": {
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            submitForm();
            return true;
        }
        return false;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}