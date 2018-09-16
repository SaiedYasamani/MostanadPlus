package com.mostanad.plus.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import com.mostanad.plus.R;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.SingleCommentModel;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;

import static android.content.Context.MODE_PRIVATE;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ViewHolder> {
    private ArrayList<SingleCommentModel> clip = new ArrayList<>();

    private Context context;
    private Typeface typeface;
    private SharedPreferences personInfo;
    private RestHelper restHelper;


    public CommentReplyAdapter(ArrayList<SingleCommentModel> clip, Context context) {
        this.clip = clip;
        this.context = context;
        restHelper=new RestHelper();
        personInfo =context.getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
    }

    @Override
    public CommentReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comments_reply, viewGroup, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(CommentReplyAdapter.ViewHolder viewHolder, int i) {

        final int k = i;

        viewHolder.name.setText(clip.get(i).getName());
        String s = Utilities.getTimeAgo(Long.parseLong(clip.get(i).getTime()));
        viewHolder.publishtime.setText(s);
        viewHolder.comment.setText(clip.get(i).getContent());

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/shabnam.ttf");
        viewHolder.name.setTypeface(typeface);
        viewHolder.publishtime.setTypeface(typeface);
        viewHolder.comment.setTypeface(typeface);
        viewHolder.report.setTypeface(typeface);

        viewHolder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aldialogreport(k);
            }
        });
    }

    public void aldialogreport(final int s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("آیا برای گزارش این نظر اطمینان دارید؟")
                .setCancelable(false)
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restHelper.ReportComment(clip.get(s).getId(), personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseVisitFinishListener() {
                                    @Override
                                    public void onFinish(JsonElement singleClips) {
                                        Toast.makeText(context, "گزارش تخلف برای این نظر ثبت شد.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                , new RestCallBack.ResponseErrorListener() {
                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTypeface(typeface);
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);

    }

    @Override
    public int getItemCount() {
        return clip != null ? clip.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, publishtime, comment, report;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.row_comment_name);
            publishtime = view.findViewById(R.id.row_comment_time);
            comment = view.findViewById(R.id.row_comment_comment);
            report = view.findViewById(R.id.row_report);

        }
    }

}