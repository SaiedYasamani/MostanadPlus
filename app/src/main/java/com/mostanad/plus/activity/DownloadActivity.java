package com.mostanad.plus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FilenameFilter;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.DownloadedVideoAdapter;
import com.mostanad.plus.utils.Constants;

public class DownloadActivity extends AppCompatActivity {


    @BindView(R.id.img_back)
    ImageView imgback;
    @BindView(R.id.txt_title)
    TextView txtdownload;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.txt_message)
    TextView txtdownloadmsg;
    @BindView(R.id.list)
    ListView list1;
    @BindView(R.id.activity_profile)
    RelativeLayout activityProfile;

    File file = new File(Environment.getExternalStorageDirectory().toString() + "/"+ Constants.APP_NAME_EN);
    String[] name;
    Bitmap[] img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!file.exists()) {
            file.mkdir();
        }
        if (file.listFiles().length > 0) {
            File list[] = file.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String filename) {
                    // TODO Auto-generated method stub
                    return filename.contains(".mp4");
                }
            });
            if (list.length > 0) {
                name = new String[list.length];
                img = new Bitmap[list.length];
                for (int i = 0; i < list.length; i++) {
                    name[i] = String.valueOf(list[i].getName().subSequence(0, list[i].getName().length() - 4));
                    img[i] = ThumbnailUtils.createVideoThumbnail(list[i].getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                }
            }

            DownloadedVideoAdapter adapter = new DownloadedVideoAdapter(this, name, img);
            list1.setAdapter(adapter);
            if (list1.getCount() > 0) {
                txtdownloadmsg.setVisibility(View.GONE);
            } else {
                txtdownloadmsg.setVisibility(View.VISIBLE);
            }

            list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String furl = file + "/" + name[position] + ".mp4";
                    Intent intent = new Intent(DownloadActivity.this, PlayVideoActivity.class);
                    intent.putExtra("url", furl.replace(" ","%20"));
                    startActivity(intent);

                }
            });

        }
    }

}