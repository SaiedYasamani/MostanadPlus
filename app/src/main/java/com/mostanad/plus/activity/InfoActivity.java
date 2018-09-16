package com.mostanad.plus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.utils.Constants;

public class InfoActivity extends AppCompatActivity {

    @BindView(R.id.img_back)
    ImageView imgback;
    @BindView(R.id.txt_profile)
    TextView txtprofile;
    @BindView(R.id.web_view)
    WebView webview;
    @BindView(R.id.activity_profile)
    LinearLayout activityProfile;
    private String type;
    String customHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            if (getIntent().hasExtra("type")) {
                type = getIntent().getExtras().getString("type");
            }
        }

        imgback = findViewById(R.id.img_back);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        switch (type) {
            case "about":
                txtprofile.setText(getString(R.string.AboutUs));
                customHtml = Constants.URL_ABOUT;
                break;
            case "contact":
                txtprofile.setText(getString(R.string.ContactUs));
                customHtml = Constants.URL_CONTACTUS;
                break;
            case "terms":
                txtprofile.setText(getString(R.string.Terms));
                customHtml = Constants.URL_RULE;
                break;
        }
        webview.loadUrl(customHtml);
    }
}
