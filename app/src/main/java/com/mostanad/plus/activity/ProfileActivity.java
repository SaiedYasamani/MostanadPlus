package com.mostanad.plus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.utils.Constants;

public class ProfileActivity extends AppCompatActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.txt_profile_name)
    TextView txtProfileName;
    @BindView(R.id.txt_profile_number)
    TextView txtProfileNumber;
    @BindView(R.id.btn_profile_edit)
    Button btnProfileEdit;
    @BindView(R.id.activity_profile)
    LinearLayout activityProfile;
    private SharedPreferences personInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        personInfo = getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);

        txtProfileNumber.setText(personInfo.getString(Constants.PHONE_NUMBER, null));
        txtProfileName.setText(personInfo.getString(Constants.USER_NAME, null));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        txtProfileNumber.setText(personInfo.getString(Constants.PHONE_NUMBER, null));
        txtProfileName.setText(personInfo.getString(Constants.USER_NAME, null));
        super.onResume();
    }
}

