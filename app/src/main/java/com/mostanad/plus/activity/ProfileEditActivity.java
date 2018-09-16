package com.mostanad.plus.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.UserModel;

import static com.mostanad.plus.utils.Constants.PASSWORD;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Constants.USER_EMAIL;
import static com.mostanad.plus.utils.Constants.USER_ID;
import static com.mostanad.plus.utils.Constants.USER_NAME;

public class ProfileEditActivity extends AppCompatActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.input_layout_name)
    TextInputLayout inputLayoutName;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.btn_profile_save)
    Button btnProfileSave;
    @BindView(R.id.activity_profile)
    RelativeLayout activityProfile;

    private SharedPreferences personInfo;
    private SharedPreferences.Editor editor;

    private String phone_number, activation_code;

    private RestHelper restHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);

        personInfo = getApplicationContext().getSharedPreferences(PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();

        restHelper = new RestHelper();
        initViews();
    }

    public void initViews() {
        phone_number = personInfo.getString(PHONE_NUMBER, "");
        activation_code = personInfo.getString(PASSWORD, "");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");

        inputLayoutName.setTypeface(typeface);
        inputLayoutEmail.setTypeface(typeface);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }
        editProfile(activation_code, edtName.getText().toString(), edtEmail.getText().toString());
    }

    private boolean validateName() {
        if (edtName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(edtName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(edtEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void editProfile(String pass, String name, String email) {
        final ProgressDialog pd = new ProgressDialog(ProfileEditActivity.this);
        pd.setMessage("در حال ارسال اطلاعات");
        pd.setCancelable(false);
        pd.show();

        restHelper.EditUser(phone_number, pass, name, email, personInfo.getString(USER_ID, ""), new RestCallBack.ResponseUserFinishListener() {
            @Override
            public void onFinish(UserModel userResponse) {
                editor.putString(USER_ID, userResponse.getData().getId());
                editor.putString(USER_NAME, userResponse.getData().getFullname());
                editor.putString(USER_EMAIL, userResponse.getData().getEmail());
                editor.putString(PASSWORD, userResponse.getData().getActivation_code());
                editor.commit();
                pd.cancel();
                finish();
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {
                if (error != null)
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                pd.cancel();
            }
        });

    }

}
