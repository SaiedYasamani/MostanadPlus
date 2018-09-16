package com.mostanad.plus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mostanad.plus.helper.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Random;

import com.mostanad.plus.R;
import com.mostanad.plus.activity.VideoActivity;
import com.mostanad.plus.pojo.SingleVideoModel;
import com.mostanad.plus.utils.Utilities;

import ir.mono.monolyticsdk.Utils.volley.toolbox.ImageLoader;
import ir.mono.monolyticsdk.Utils.volley.toolbox.NetworkImageView;

public class HomeCategoryVideosAdapter extends RecyclerView.Adapter<HomeCategoryVideosAdapter.ViewHolder> {
    private LinkedList<SingleVideoModel> clip;
    private Context context;
    ImageLoader imageLoader;

    public HomeCategoryVideosAdapter(LinkedList<SingleVideoModel> clip, Context context) {
        this.clip = clip;
        this.context = context;
    }

    @Override
    public HomeCategoryVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_clip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeCategoryVideosAdapter.ViewHolder viewHolder, final int i) {

        imageLoader = VolleySingleton.getInstance(context).getImageLoader();

        viewHolder.txt_clip_name.setText(clip.get(i).getTitle_fa());
        String s = Utilities.getTimeAgo(Long.parseLong(clip.get(i).getPublish_time()));
        viewHolder.txt_clip_time.setText(s);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

//        Picasso.with(context).load(clip.get(i).getImage()).fit().placeholder(new ColorDrawable(color)).into(viewHolder.img_clip_image);
        viewHolder.img_clip_image.setImageUrl(clip.get(i).getImage(), imageLoader);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("name", clip.get(i).getTitle_fa());
                intent.putExtra("description", clip.get(i).getDescription());
                intent.putExtra("like", clip.get(i).getLike());
                intent.putExtra("visit", clip.get(i).getVisit());
                intent.putExtra("id", clip.get(i).getId());
                intent.putExtra("file_url", clip.get(i).getFile_url());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clip.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private NetworkImageView img_clip_image;
        private TextView txt_clip_name, txt_clip_time;

        public ViewHolder(View view) {
            super(view);

            txt_clip_time = view.findViewById(R.id.txt_clip_time);
            txt_clip_name = view.findViewById(R.id.txt_clip_name);
            img_clip_image = view.findViewById(R.id.img_clip_image);
        }
    }

}