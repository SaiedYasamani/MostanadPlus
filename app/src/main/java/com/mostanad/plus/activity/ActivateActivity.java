package com.mostanad.plus.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
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
import com.mostanad.plus.interfaces.OnNewMessageListener;
import com.mostanad.plus.pojo.UserActivateModel;
import com.mostanad.plus.receiver.SmsListener;
import com.mostanad.plus.utils.MyApplication;
import com.mostanad.plus.utils.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mostanad.plus.utils.Constants.APP_NAME_FA;
import static com.mostanad.plus.utils.Constants.IP;
import static com.mostanad.plus.utils.Constants.IS_ACTIVE;
import static com.mostanad.plus.utils.Constants.IS_SPECIAL;
import static com.mostanad.plus.utils.Constants.PASSWORD;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Constants.PLAYER_ID;
import static com.mostanad.plus.utils.Constants.SPECIAL_MSG;
import static com.mostanad.plus.utils.Constants.USER_ID;
import static com.mostanad.plus.utils.Utilities.isNumber;

public class ActivateActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    @BindView(R.id.txt_mobmsg)
    TextView txtMobmsg;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.txt_change_number)
    TextView txtChangeNumber;


    private SharedPreferences personInfo;
    private SharedPreferences.Editor editor;

    private String imei;
    private RestHelper restHelper;

    private OnNewMessageListener newMessageListener;
    private SmsListener smsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        ButterKnife.bind(this);
        restHelper = new RestHelper();

        edtPhone.setOnEditorActionListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        Log.e("IMEI OF DEVICE", imei);

        personInfo = getApplicationContext().getSharedPreferences(PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");

        inputLayoutPhone.setHintEnabled(false);

        inputLayoutPhone.setTypeface(typeface);

        if (Utilities.IsAutoDetect()) {
            newMessageListener = new OnNewMessageListener() {
                @Override
                public void onNewMessageRecived(String messageNumber) {
                    edtPhone.setText(messageNumber);
//                    unregisterReceiver(smsListener);
                    if (isNumber(edtPhone.getText().toString())) {
                        submitForm();
                    } else edtPhone.setError("کد فعالسازی وارد شده صحیح نیست");
                }
            };
//            String regex = personInfo.getString(Constants.PREF_REGEX, "");
//            if (regex.equals(""))
//                regex = null;
//            smsListener = new SmsListener(newMessageListener, regex);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                registerReceiver(smsListener, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
                ((MyApplication) getApplication()).getSmsListener().setOnNewMessageListener(newMessageListener);
//                smsListener.setOnNewMessageListener(newMessageListener);

            }
        }


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        txtChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(PHONE_NUMBER, "");
                editor.putBoolean(IS_ACTIVE, false);
                editor.apply();
                Intent intent = new Intent(ActivateActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void submitForm() {
        hideKeyboard();
        if (!validatePhone()) {
            return;
        }
        activateUser();
    }

    @Override
    protected void onStop() {
        newMessageListener = null;
        super.onStop();
    }

    private boolean validatePhone() {
        if (edtPhone.getText().toString().trim().isEmpty()) {
            edtPhone.setError(getString(R.string.err_msg_phone2));
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    private void activateUser() {
        final ProgressDialog pd = new ProgressDialog(ActivateActivity.this);
        pd.setMessage("در حال ارسال اطلاعات");
        pd.setCancelable(false);
        pd.show();

        restHelper.Activate(personInfo.getString(PHONE_NUMBER, ""), edtPhone.getText().toString(), personInfo.getString(IP, ""), imei, personInfo.getString(PLAYER_ID, ""), Utilities.GetSensub(), new RestCallBack.ResponseActivateFinishListener() {
            @Override
            public void onFinish(UserActivateModel response, int code) {
                Log.d("Debug", "response : " + response.getCode());

                switch (code) {
                    case 200: {
                        Toast.makeText(getApplicationContext(), "به " + APP_NAME_FA + " خوش آمدید", Toast.LENGTH_SHORT).show();
                        editor.putBoolean(IS_ACTIVE, true);
                        editor.putString(USER_ID, String.valueOf(response.getUser_id()));
                        editor.putString(PASSWORD, edtPhone.getText().toString());
                        editor.putBoolean(IS_SPECIAL, response.isIs_special());
                        if (response.getSpecial_msg() != null)
                            editor.putString(SPECIAL_MSG, response.getSpecial_msg());

                        editor.apply();
                        pd.cancel();
                        Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
                        startActivity(intent);
                        if (personInfo.getString(PHONE_NUMBER, "").startsWith("091") || personInfo.getString(PHONE_NUMBER, "").startsWith("099"))
                            AnalyticHandler.logActivate(ActivateActivity.this);
                        finish();
                        break;
                    }

                    case 202: {
                        Toast.makeText(getApplicationContext(), "به " + APP_NAME_FA + " خوش آمدید", Toast.LENGTH_SHORT).show();
                        editor.putBoolean(IS_ACTIVE, true);
                        editor.putString(USER_ID, String.valueOf(response.getUser_id()));
                        editor.putString(PASSWORD, edtPhone.getText().toString());
                        editor.putBoolean(IS_SPECIAL, response.isIs_special());
                        if (response.getSpecial_msg() != null)
                            editor.putString(SPECIAL_MSG, response.getSpecial_msg());

                        editor.apply();
                        pd.cancel();
                        Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
                        startActivity(intent);
                        if (personInfo.getString(PHONE_NUMBER, "").startsWith("091") || personInfo.getString(PHONE_NUMBER, "").startsWith("099"))
                            AnalyticHandler.logActivate(ActivateActivity.this);
                        finish();
                        break;
                    }
                    case 302: {
                        Toast.makeText(getApplicationContext(), "به " + APP_NAME_FA + " خوش آمدید", Toast.LENGTH_SHORT).show();
                        editor.putBoolean(IS_ACTIVE, true);
                        editor.putString(PASSWORD, edtPhone.getText().toString());
                        editor.apply();
                        pd.cancel();
                        Intent intent = new Intent(ActivateActivity.this, MainActivity.class);
                        startActivity(intent);
                        if (personInfo.getString(PHONE_NUMBER, "").startsWith("091") || personInfo.getString(PHONE_NUMBER, "").startsWith("099"))
                            AnalyticHandler.logActivate(ActivateActivity.this);
                        finish();
                        break;
                    }
                }
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {

                Log.d("Debug", "error : " + error);
                switch (error) {
                    case "400": {
                        Toast.makeText(getApplicationContext(), "شماره تلفن وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                        editor.putString(PHONE_NUMBER, "");
                        editor.apply();
                        Intent intent = new Intent(ActivateActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "404": {
                        Toast.makeText(getApplicationContext(), "کاربر در سامانه وجود ندارد", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                        editor.putString(PHONE_NUMBER, "");
                        editor.apply();
                        Intent intent = new Intent(ActivateActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "406":
                        Toast.makeText(getApplicationContext(), "کد فعالسازی وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
                        pd.cancel();
                        break;

                    case "429": {
                        Toast.makeText(getApplicationContext(), "حساب کاربری شما بدلیل وارد کردن کد اشتباه به مدت پنج دقیقه قفل میباشد", Toast.LENGTH_SHORT).show();
                        pd.cancel();
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