package com.mostanad.plus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.NewVideosAdapter;
import com.mostanad.plus.helper.EndlessRecyclerViewScrollListener;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.SingleVideoModel;
import com.mostanad.plus.pojo.VideosModel;
import com.mostanad.plus.utils.Constants;



public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.pgbar)
    ProgressBar prbar;
    @BindView(R.id.txt_wait)
    TextView txtwait;
    @BindView(R.id.ll_wait)
    LinearLayout llwait;
    @BindView(R.id.img_back)
    ImageView imgback;
    @BindView(R.id.txt_query_word)
    TextView txtcat;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.rvCategory)
    RecyclerView rvCategory;
    @BindView(R.id.pgbar_load_more)
    SmoothProgressBar progressbar;
    @BindView(R.id.rlcategory)
    RelativeLayout rlcategory;
    @BindView(R.id.txt_msg)
    TextView txtmsg;

    private RestHelper restHelper;
    public NewVideosAdapter adapter;
    private String categoryId, categoryName, label = "0";

    private GridLayoutManager mLayoutManager;

    private LinkedList<SingleVideoModel> videoList=new LinkedList<>();
    private EndlessRecyclerViewScrollListener scrollListener;
    private SharedPreferences personInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        categoryId = extras.getString("categoryId");
        categoryName = extras.getString("categoryName");

        if (getIntent().hasExtra("label"))
            label = extras.getString("label");

        restHelper = new RestHelper();
        initViews();
    }

    public void initViews() {
        personInfo = getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);

        txtcat.setText(categoryName);
        rvCategory.setHasFixedSize(true);
        rvCategory.setItemViewCacheSize(20);
        rvCategory.setDrawingCacheEnabled(true);
        rvCategory.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mLayoutManager = new GridLayoutManager(CategoryActivity.this, 2);
        rvCategory.setLayoutManager(mLayoutManager);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (label.contentEquals("notif")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (label.contentEquals("notif")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
                break;
        }
    }

    public void loadData() {
        restHelper.CategoryClips(20, 1, Constants.NEWEST_TYPE, categoryId, personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""),
                new RestCallBack.ResponseSearchFinishListener() {
                    @Override
                    public void onFinish(VideosModel singleClips) {
                        initializeRecyclerView(singleClips);
                        llwait.setVisibility(View.GONE);
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        llwait.setVisibility(View.GONE);
                        txtmsg.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initializeRecyclerView(final VideosModel singleClips) {

        videoList.addAll(singleClips.getData());
        adapter = new NewVideosAdapter(videoList, getApplicationContext());
        rvCategory.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                progressbar.setVisibility(View.VISIBLE);
                restHelper.CategoryClips(20, page + 1, Constants.NEWEST_TYPE, categoryId, personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""),
                        new RestCallBack.ResponseSearchFinishListener() {
                            @Override
                            public void onFinish(VideosModel singleClips) {
                                if (singleClips != null) {
                                    videoList.addAll(singleClips.getData());
                                    adapter.notifyDataSetChanged();
                                }
                                progressbar.setVisibility(View.GONE);

                            }
                        }, new RestCallBack.ResponseErrorListener() {
                            @Override
                            public void onError(String error) {
                                if (error != null)
                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                progressbar.setVisibility(View.GONE);
                            }
                        });
            }
        };
        rvCategory.addOnScrollListener(scrollListener);
    }
}
