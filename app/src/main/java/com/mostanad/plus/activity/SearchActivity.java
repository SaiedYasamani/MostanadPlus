package com.mostanad.plus.activity;

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

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    public NewVideosAdapter adapter;
    @BindView(R.id.pgbar)
    ProgressBar pgbar;
    @BindView(R.id.txt_wait)
    TextView txtWait;
    @BindView(R.id.ll_wait)
    LinearLayout llWait;
    @BindView(R.id.txt_msg)
    TextView txtMsg;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_query_word)
    TextView txtQueryWord;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    @BindView(R.id.pgbar_load_more)
    SmoothProgressBar pgbarLoadMore;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    private LinkedList<SingleVideoModel> searchList=new LinkedList<>();

    private GridLayoutManager layoutManager;
    private String query;

    private EndlessRecyclerViewScrollListener scrollListener;

    private RestHelper restHelper;

    private SharedPreferences personInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Bundle ex = getIntent().getExtras();
        query = ex.getString("query");
        restHelper = new RestHelper();
        initViews();
    }

    public void initViews() {
        personInfo = getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);

        txtQueryWord.setText(query);

        imgBack.setOnClickListener(this);

        rvSearch.setHasFixedSize(true);
        rvSearch.setItemViewCacheSize(20);
        rvSearch.setDrawingCacheEnabled(true);
        rvSearch.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layoutManager = new GridLayoutManager(SearchActivity.this, 2);
        rvSearch.setLayoutManager(layoutManager);

        loadData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    public void loadData() {
        restHelper.SearchVideo(Constants.SEARCH_TYPE, query, 1, 20, personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseSearchFinishListener() {
                    @Override
                    public void onFinish(VideosModel singleClips) {
                        initializeRecyclerView(singleClips);
                        llWait.setVisibility(View.GONE);
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                        llWait.setVisibility(View.GONE);
                        txtMsg.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initializeRecyclerView(final VideosModel singleClips) {
        searchList.addAll(singleClips.getData());
        adapter = new NewVideosAdapter(searchList, SearchActivity.this);
        rvSearch.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pgbarLoadMore.setVisibility(View.VISIBLE);
                restHelper.SearchVideo(Constants.SEARCH_TYPE, query, page + 1, 20, personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseSearchFinishListener() {
                    @Override
                    public void onFinish(VideosModel singleClips) {
                        if (singleClips != null){
                            searchList.addAll(singleClips.getData());
                            adapter.notifyDataSetChanged();
                        }
                        pgbarLoadMore.setVisibility(View.GONE);
                    }
                }, new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                        pgbarLoadMore.setVisibility(View.GONE);

                    }
                });
            }
        };
        rvSearch.addOnScrollListener(scrollListener);
    }
}
