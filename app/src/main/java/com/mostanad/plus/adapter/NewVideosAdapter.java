package com.mostanad.plus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Random;

import com.mostanad.plus.R;
import com.mostanad.plus.activity.VideoActivity;
import com.mostanad.plus.pojo.SingleVideoModel;

import static com.mostanad.plus.utils.Utilities.getTimeAgo;

public class NewVideosAdapter extends RecyclerView.Adapter<NewVideosAdapter.ViewHolder> {
    private LinkedList<SingleVideoModel> clip;
    private Context context;
    private Typeface typeface;

    public NewVideosAdapter(LinkedList<SingleVideoModel> clip, Context context) {
        this.clip = clip;
        this.context = context;
    }

    @Override
    public NewVideosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_mostview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewVideosAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.name.setText(clip.get(i).getTitle_fa());
        String s = getTimeAgo(Long.parseLong(clip.get(i).getPublish_time()));
        viewHolder.publishtime.setText(s);
        viewHolder.visit.setText(clip.get(i).getVisit());
        viewHolder.like.setText(clip.get(i).getLike());

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        Picasso.with(context).load(clip.get(i).getImage()).fit().placeholder(new ColorDrawable(color)).into(viewHolder.img);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/shabnam.ttf");
        viewHolder.name.setTypeface(typeface);
        viewHolder.publishtime.setTypeface(typeface);
        viewHolder.visit.setTypeface(typeface);
        viewHolder.like.setTypeface(typeface);

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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clip.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView name, publishtime, visit, like;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.row_most_name);
            publishtime = view.findViewById(R.id.row_most_time);
            visit = view.findViewById(R.id.row_most_bazdid);
            like = view.findViewById(R.id.row_most_like2);
            img = view.findViewById(R.id.row_most_img);
        }
    }

}