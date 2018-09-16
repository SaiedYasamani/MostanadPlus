package com.mostanad.plus.activity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.longtailvideo.jwplayer.JWPlayerFragment;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.events.listeners.AdvertisingEvents;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.ClipReportAdapter;
import com.mostanad.plus.adapter.CommentsAdapter;
import com.mostanad.plus.adapter.NewVideosAdapter;
import com.mostanad.plus.helper.HashGeneratorUtils;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.AddCommentModel;
import com.mostanad.plus.pojo.CommentsModel;
import com.mostanad.plus.pojo.SetLikeModel;
import com.mostanad.plus.pojo.SingleCommentModel;
import com.mostanad.plus.pojo.VideosModel;
import com.mostanad.plus.view.RegisterSpecialAlertDialogView;

import static com.mostanad.plus.utils.Constants.APP_NAME_EN;
import static com.mostanad.plus.utils.Constants.APP_NAME_FA;
import static com.mostanad.plus.utils.Constants.IP;
import static com.mostanad.plus.utils.Constants.IS_SPECIAL;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Constants.SEARCH_TYPE;
import static com.mostanad.plus.utils.Constants.USER_EMAIL;
import static com.mostanad.plus.utils.Constants.USER_ID;
import static com.mostanad.plus.utils.Constants.USER_NAME;

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.rl_jwplayer)
    RelativeLayout rlJwplayer;
    @BindView(R.id.txt_video_title)
    TextView txtVideoTitle;
    @BindView(R.id.img_video_report)
    ImageView imgVideoReport;
    @BindView(R.id.txt_video_visits)
    TextView txtVideoVisits;
    @BindView(R.id.txt_video_description)
    TextView txtVideoDescription;
    @BindView(R.id.img_video_like)
    ImageView imgVideoLike;
    @BindView(R.id.txt_video_like_count)
    TextView txtVideoLikeCount;
    @BindView(R.id.img_video_share)
    ImageView imgVideoShare;
    @BindView(R.id.img_video_download)
    ImageView imgVideoDownload;
    @BindView(R.id.img_video_comment)
    ImageView imgVideoComment;
    @BindView(R.id.txt_related_video)
    TextView txtRelatedVideo;
    @BindView(R.id.rv_related_videos)
    RecyclerView rvRelatedVideos;
    @BindView(R.id.pbar_related_videos)
    ProgressBar pbarRelatedVideos;
    @BindView(R.id.txt_video_comments)
    TextView txtVideoComments;
    @BindView(R.id.txt_no_comments_msg)
    TextView txtNoCommentsMsg;
    @BindView(R.id.rv_video_comments)
    RecyclerView rvVideoComments;
    @BindView(R.id.pbar_rv_comments)
    ProgressBar pbarRvComments;

    private EditText edtUserComment, edtUserName;

    private ArrayMap<String, SingleCommentModel> commentArrayMap = new ArrayMap<>();
    private SingleCommentModel dataComment;
    private NewVideosAdapter relatedVideoAdapter;
    private CommentsAdapter adapter;

    private SharedPreferences personInfo;
    private SharedPreferences.Editor editor;

    private JWPlayerView playerView;

    private Typeface typeface;
    private String newLikeCount, videoId, imei, videoUrl = "", phoneNumber, userName, userEmail, label = "0", videoSize = "نامشخص";

    private File dir = new File(Environment.getExternalStorageDirectory(), APP_NAME_EN);

    private int imageId = R.drawable.ic_heart_grey_24dp;
    private RestHelper restHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);
        ButterKnife.bind(this);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        restHelper = new RestHelper();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");

        personInfo = getApplicationContext().getSharedPreferences(PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();

        if (personInfo.contains(IS_SPECIAL) && personInfo.getBoolean(IS_SPECIAL, false)) {
            RegisterSpecialAlertDialogView registerSpecialAlertDialogView = new RegisterSpecialAlertDialogView(VideoActivity.this);
            registerSpecialAlertDialogView.show();
        }

        userName = personInfo.getString(USER_NAME, "کاربر ناشناس");
        userEmail = personInfo.getString(USER_EMAIL, "");

        phoneNumber = personInfo.getString(PHONE_NUMBER, "");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        imei = HashGeneratorUtils.generateSHA256(wInfo.getMacAddress());

        imgVideoReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (getIntent() != null && getIntent().getExtras() != null && extras.getString("me.cheshmak.data") != null) {
            try {
                JSONObject object = new JSONObject(extras.getString("me.cheshmak.data"));
                videoId = object.getString("id");
                txtVideoTitle.setText(object.getString("name"));
                txtVideoVisits.setText(object.getString("visit"));
                txtVideoDescription.setText(Html.fromHtml(object.getString("description")));
                txtVideoLikeCount.setText(object.getString("like"));
                videoUrl = object.getString("file_url");
                if (object.has("label"))
                    label = object.getString("label");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            videoId = extras.getString("id");
            txtVideoTitle.setText(extras.getString("name"));
            txtVideoVisits.setText(extras.getString("visit"));
            txtVideoDescription.setText(Html.fromHtml(extras.getString("description")));
            txtVideoLikeCount.setText(extras.getString("like"));
            videoUrl = extras.getString("file_url");
            if (getIntent().hasExtra("label"))
                label = extras.getString("label");
        }

        rvRelatedVideos.setHasFixedSize(true);
        rvRelatedVideos.setItemViewCacheSize(20);
        rvRelatedVideos.setDrawingCacheEnabled(true);
        rvRelatedVideos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager linearLayoutManager2 = new GridLayoutManager(VideoActivity.this, 2);
        rvRelatedVideos.setLayoutManager(linearLayoutManager2);
        rvRelatedVideos.setNestedScrollingEnabled(false);

        rvVideoComments.setHasFixedSize(true);
        rvVideoComments.setItemViewCacheSize(20);
        rvVideoComments.setDrawingCacheEnabled(true);
        rvVideoComments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvVideoComments.setLayoutManager(linearLayoutManager);
        rvVideoComments.setNestedScrollingEnabled(false);

        imgVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNAME = personInfo.getString(USER_NAME, "");
                if (userNAME.equals("")) {
                    editor.putString(USER_NAME, "کاربر ناشناس");
                    editor.apply();
                }
                aldialog();
            }
        });

        imgVideoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageId == R.drawable.ic_heart_grey_24dp) {
                    setLikeClip();
                } else {
                    Toast.makeText(VideoActivity.this, "شما قبلا این کلیپ را لایک کرده اید", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgVideoDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), "MostanadPlus/" + txtVideoTitle.getText().toString() + ".mp4");
                if (file.exists()) {
                    Toast.makeText(getApplicationContext(), "این ویدیو قبلا دانلود شده است. لطفا به بخش دانلود ها مراجعه کنید", Toast.LENGTH_LONG).show();
                } else {
                    playerView.pause();
                    downloadVideo();
                }
            }
        });
        imgVideoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, APP_NAME_FA);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "مستند پلاس شما را به دیدن این ویدیو دعوت میکند" + videoUrl);
                startActivity(Intent.createChooser(sendIntent, "ارسال توسط"));
                imgVideoShare.setImageResource(R.drawable.ic_share_blue_24dp);
            }
        });

        // Get a handle to the JWPlayerFragment
        JWPlayerFragment fragment = (JWPlayerFragment) getFragmentManager().findFragmentById(R.id.fm_jwplayer);

        // Get a handle to the JWPlayerView
        playerView = fragment.getPlayer();
        // Create a PlaylistItem
        PlaylistItem video = new PlaylistItem(videoUrl);

        // Load a stream into the player
        playerView.load(video);
        // playerView.setup(playerConfig.build());
        playerView.addOnBeforePlayListener(new AdvertisingEvents.OnBeforePlayListener() {
            @Override
            public void onBeforePlay() {
                visitClip();
                isLikedClip();
                loadRelatedClips(txtVideoTitle.getText().toString());
                getComments();
            }
        });

        if (!personInfo.getBoolean(IS_SPECIAL,false))
            playerView.play();
    }

    private void showDialog() {
        final String[] items = {
                "گزارش خرابی", "گزارش محتوا"
        };
        final Dialog dialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_report, null);

        ListView lv = view.findViewById(R.id.list_report);
        TextView textView = view.findViewById(R.id.txt_report_dialog_title);
        textView.setText("یک دلیل برای گزارش تخلف انتخاب کنید");

        ClipReportAdapter clad = new ClipReportAdapter(VideoActivity.this, items);

        lv.setAdapter(clad);
        final String userID = personInfo.getString(USER_ID, "");
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                restHelper.ReportClip(videoId, String.valueOf(i + 1), userID, phoneNumber, new RestCallBack.ResponseVisitFinishListener() {
                    @Override
                    public void onFinish(JsonElement visits) {
                        Toast.makeText(VideoActivity.this, items[i] + " برای این ویدیو ثبت شد", Toast.LENGTH_SHORT).show();
                    }
                }, new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {

                    }
                });
                dialog.dismiss();

            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.show();

    }

    public ArrayMap<String, SingleCommentModel> getData(CommentsModel commentsModel) {
        ArrayMap<String, SingleCommentModel> dataObject = new ArrayMap<>();
        JsonObject data = commentsModel.getData();

        for (Iterator<Map.Entry<String, JsonElement>> entries = data.entrySet().iterator(); entries.hasNext(); ) {
            Map.Entry<String, JsonElement> entry = entries.next();
            String key = entry.getKey();
            SingleCommentModel fields;
            fields = new Gson().fromJson(entry.getValue(), SingleCommentModel.class);
            dataObject.put(key, fields);
        }
        return dataObject;
    }

    private void getComments() {
        restHelper.GetComments(videoId, personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseCommentsFinishListener() {
            @Override
            public void onFinish(CommentsModel commentsModel) {
                switch (commentsModel.getCode()) {
                    case "200":
                        commentArrayMap = getData(commentsModel);
                        adapter = new CommentsAdapter(commentArrayMap, VideoActivity.this);
                        rvVideoComments.setAdapter(adapter);
                        pbarRvComments.setVisibility(View.GONE);
                        txtNoCommentsMsg.setVisibility(View.GONE);
                        break;
                    case "404": {
                        pbarRvComments.setVisibility(View.GONE);
                        adapter = new CommentsAdapter(commentArrayMap, VideoActivity.this);
                        rvVideoComments.setAdapter(adapter);
                        txtNoCommentsMsg.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "400": {
                        pbarRvComments.setVisibility(View.GONE);
                        txtNoCommentsMsg.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {
                switch (error) {
                    case "404": {
                        pbarRvComments.setVisibility(View.GONE);
                        adapter = new CommentsAdapter(commentArrayMap, VideoActivity.this);
                        rvVideoComments.setAdapter(adapter);
                        txtNoCommentsMsg.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "400": {
                        pbarRvComments.setVisibility(View.GONE);
                        txtNoCommentsMsg.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });
    }

    public void addComment() {
        restHelper.AddComment(videoId, "0", userName, userEmail, edtUserComment.getText().toString(), personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseAddCommentFinishListener() {
                    @Override
                    public void onFinish(AddCommentModel success) {
                        switch (success.getCode()) {
                            case "200": {
                                dataComment = success.getData();
                                String status = dataComment.getStatus();
                                if (status.contentEquals("0")) {
                                    Toast.makeText(getApplicationContext(), "نظر شما ثبت شد و پس از تایید انتشار خواهد یافت", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "نظر شما ثبت شد", Toast.LENGTH_LONG).show();
//                                    data.add(0, dataComment);
                                    commentArrayMap.put(dataComment.getId(), dataComment);
                                    txtNoCommentsMsg.setVisibility(View.GONE);
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                }

                                Toast.makeText(getApplicationContext(), "نظر شما ثبت شد", Toast.LENGTH_LONG).show();
                                imgVideoComment.setImageResource(R.drawable.ic_comment_green_24dp);
                                break;
                            }

                            case "405": {
                                Toast.makeText(getApplicationContext(), "متن نظر خالی میباشد", Toast.LENGTH_LONG).show();
                                break;
                            }
                            case "400": {
                                Toast.makeText(getApplicationContext(), "کاربر یافت نشد", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        switch (error) {
                            case "405": {
                                Toast.makeText(getApplicationContext(), "متن نظر خالی میباشد", Toast.LENGTH_LONG).show();
                                break;
                            }
                            case "400": {
                                Toast.makeText(getApplicationContext(), "کاربر یافت نشد", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                });
    }

    private void setLikeClip() {

        String mobip = personInfo.getString(IP, "").trim();

        restHelper.SetLiked(videoId, mobip, imei, personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseLikeFinishListener() {
                    @Override
                    public void onFinish(SetLikeModel success) {
                        switch (success.getCode()) {
                            case "200": {
                                Toast.makeText(getApplicationContext(), "کلیپ لایک شد", Toast.LENGTH_LONG).show();
                                imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                imageId = R.drawable.ic_heart_red_24dp;
                                int i = Integer.parseInt(txtVideoLikeCount.getText().toString());
                                i++;
                                newLikeCount = i + "";
                                txtVideoLikeCount.setText(newLikeCount);
                                break;
                            }

                            case "405": {
                                Toast.makeText(getApplicationContext(), "شما قبلا این کلیپ را لایک کرده اید", Toast.LENGTH_LONG).show();
                                imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                imageId = R.drawable.ic_heart_red_24dp;
                                break;
                            }
                            case "400": {
                                imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                int i = Integer.parseInt(txtVideoLikeCount.getText().toString());
                                i++;
                                newLikeCount = i + "";
                                txtVideoLikeCount.setText(newLikeCount);
                                imageId = R.drawable.ic_heart_red_24dp;
                                break;
                            }
                        }
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        switch (error) {
                            case "200": {
                                Toast.makeText(getApplicationContext(), "کلیپ لایک شد", Toast.LENGTH_LONG).show();
                                imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                imageId = R.drawable.ic_heart_red_24dp;
                                int i = Integer.parseInt(txtVideoLikeCount.getText().toString());
                                i++;
                                newLikeCount = i + "";
                                txtVideoLikeCount.setText(newLikeCount);
                                break;
                            }

                            case "405": {
                                Toast.makeText(getApplicationContext(), "شما قبلا این کلیپ را لایک کرده اید", Toast.LENGTH_LONG).show();
                                imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                imageId = R.drawable.ic_heart_red_24dp;
                                break;
                            }
                            case "400": {
                                imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                int i = Integer.parseInt(txtVideoLikeCount.getText().toString());
                                i++;
                                newLikeCount = i + "";
                                txtVideoLikeCount.setText(newLikeCount);
                                imageId = R.drawable.ic_heart_red_24dp;
                                break;
                            }
                        }
                    }
                });
    }

    private void isLikedClip() {
        String mobip = personInfo.getString(IP, "").trim();
        String mphone = personInfo.getString(USER_ID, "");
        if (!mphone.equals("")) {
            restHelper.IsLiked(videoId, mobip, imei, personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseFinishListener() {
                        @Override
                        public void onFinish(String success) {
                            switch (success) {
                                case "404":
                                    imgVideoLike.setImageResource(R.drawable.ic_heart_grey_24dp);
                                    imageId = R.drawable.ic_heart_grey_24dp;
                                    break;
                                case "302":
                                    imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                    imageId = R.drawable.ic_heart_red_24dp;
                                    break;
                                case "400":
                                    imgVideoLike.setImageResource(R.drawable.ic_heart_grey_24dp);
                                    imageId = R.drawable.ic_heart_grey_24dp;
                                    break;
                            }
                        }
                    }
                    , new RestCallBack.ResponseErrorListener() {
                        @Override
                        public void onError(String error) {
                            switch (error) {
                                case "404":
                                    imgVideoLike.setImageResource(R.drawable.ic_heart_grey_24dp);
                                    imageId = R.drawable.ic_heart_grey_24dp;
                                    break;
                                case "302":
                                    imgVideoLike.setImageResource(R.drawable.ic_heart_red_24dp);
                                    imageId = R.drawable.ic_heart_red_24dp;
                                    break;
                                case "400":
                                    imgVideoLike.setImageResource(R.drawable.ic_heart_grey_24dp);
                                    imageId = R.drawable.ic_heart_grey_24dp;
                                    break;
                            }
                        }
                    });
        }
    }

    private void visitClip() {
        restHelper.Visit(videoId, personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseVisitFinishListener() {
                    @Override
                    public void onFinish(JsonElement singleClips) {

                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void loadRelatedClips(String q) {

        restHelper.SearchRelatedVideo(SEARCH_TYPE, q, q, 2, 4, personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseSearchFinishListener() {
                    @Override
                    public void onFinish(VideosModel singleClips) {
                        pbarRelatedVideos.setVisibility(View.GONE);
                        relatedVideoAdapter = new NewVideosAdapter(singleClips.getData(), VideoActivity.this);
                        rvRelatedVideos.setAdapter(relatedVideoAdapter);
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        pbarRelatedVideos.setVisibility(View.GONE);
                    }
                });
    }

    public void aldialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(VideoActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_add_comment, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(VideoActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        edtUserComment = mView.findViewById(R.id.edt_user_comment);
        edtUserComment.setTypeface(typeface);
        edtUserName = mView.findViewById(R.id.edt_user_name);
        edtUserName.setTypeface(typeface);
        if (userName.equals("")) {
            edtUserName.setText("");
        } else {
            edtUserName.setText(userName);
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ثبت", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (edtUserName.getText().toString().length() > 0)
                            userName = edtUserName.getText().toString();
                        addComment();
                        dialogBox.dismiss();
                    }
                })

                .setNegativeButton("لغو",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

        alertDialogAndroid.show();
        alertDialogAndroid.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
        alertDialogAndroid.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

    private void downloadVideo() {
        if (!dir.exists())
            dir.mkdir();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
        // appears the same in Notification bar while downloading
        request.setDescription("در حال دانلود")
                .setTitle(txtVideoTitle.getText())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                // save the file in the "Downloads" folder of SDCARD
                .setDestinationInExternalPublicDir(APP_NAME_EN, String.valueOf(txtVideoTitle.getText() + ".mp4"))
                .allowScanningByMediaScanner();
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            File file = new File(dir + "/" + txtVideoTitle.getText().toString() + ".mp4");
            if (file.exists())
                Toast.makeText(getApplicationContext(), "دانلود کامل شد", Toast.LENGTH_LONG).show();
            imgVideoDownload.setImageResource(R.drawable.ic_download_yellow_24dp);
//            unregisterReceiver(onComplete);
        }
    };

    @Override
    protected void onStop() {
        if (onComplete != null) {
            unregisterReceiver(onComplete);
            onComplete = null;
        }
        super.onStop();
    }
}
