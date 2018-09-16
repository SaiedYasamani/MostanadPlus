package com.mostanad.plus.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import com.mostanad.plus.R;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.AddCommentModel;
import com.mostanad.plus.pojo.SingleCommentModel;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;

import static android.content.Context.MODE_PRIVATE;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private ArrayMap<String, SingleCommentModel> commentArrayMap;

    private Context context;
    private EditText userInputDialogEditText, userInputDialogName;
    private SharedPreferences personInfo;
    private Typeface typeface;
    private RestHelper restHelper;

    public CommentsAdapter(ArrayMap<String, SingleCommentModel> commentArrayMap, Context context) {
        this.commentArrayMap = commentArrayMap;
        this.context = context;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/shabnam.ttf");
        restHelper = new RestHelper();
        personInfo = context.getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comments, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.name.setText(commentArrayMap.get(commentArrayMap.keyAt(i)).getName());
        if (commentArrayMap.get(commentArrayMap.keyAt(i)).getTime()!=null) {
            String s = Utilities.getTimeAgo(Long.parseLong(commentArrayMap.get(commentArrayMap.keyAt(i)).getTime()));
            viewHolder.publishtime.setText(s);
        }
        viewHolder.comment.setText(commentArrayMap.get(commentArrayMap.keyAt(i)).getContent());

        viewHolder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aldialogreport(i);
            }
        });

        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aldialog(i);
            }
        });

        CommentReplyAdapter adapter = new CommentReplyAdapter(commentArrayMap.get(commentArrayMap.keyAt(i)).getChilds(), context);
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.recyclerView.setLayoutManager(layoutManager);
    }

    public void aldialogreport(final int s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("آیا برای گزارش این نظر اطمینان دارید؟")
                .setCancelable(false)
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restHelper.ReportComment(commentArrayMap.get(commentArrayMap.keyAt(s)).getId(), personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseVisitFinishListener() {
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

    public void aldialog(final int s) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_add_comment, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/shabnam.ttf");
        userInputDialogEditText = mView.findViewById(R.id.edt_user_comment);
        userInputDialogName = mView.findViewById(R.id.edt_user_name);
        userInputDialogEditText.setTypeface(typeface);
        userInputDialogName.setTypeface(typeface);
        personInfo = context.getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
        final String userID = personInfo.getString(Constants.USER_ID, "");
        final String userNAME = personInfo.getString(Constants.USER_NAME, "");
        final String userEMail = personInfo.getString(Constants.USER_EMAIL, "");
        userInputDialogName.setText(userNAME);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ثبت", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        restHelper.AddComment(commentArrayMap.get(commentArrayMap.keyAt(s)).getClip_id(), commentArrayMap.get(commentArrayMap.keyAt(s)).getId(), userNAME, userEMail, userInputDialogEditText.getText().toString(), userID, personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseAddCommentFinishListener() {
                                    @Override
                                    public void onFinish(AddCommentModel success) {
                                        switch (success.getCode()) {
                                            case "200": {
                                                String status = success.getData().getStatus();
                                                if (status.equals("0")) {
                                                    Toast.makeText(context, "نظر شما ثبت شد و پس از تایید انتشار خواهد یافت", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(context, "نظر شما ثبت شد", Toast.LENGTH_LONG).show();
                                                    if (commentArrayMap.get(commentArrayMap.keyAt(s)).getChilds() != null)
                                                        commentArrayMap.get(commentArrayMap.keyAt(s)).getChilds().add(success.getData());
                                                    else {
                                                        ArrayList<SingleCommentModel> commentArrayList = new ArrayList<>();
                                                        commentArrayList.add(success.getData());
                                                        commentArrayMap.get(commentArrayMap.keyAt(s)).setChilds(commentArrayList);
                                                    }
                                                    notifyDataSetChanged();
                                                }
                                                break;
                                            }

                                            case "405": {
                                                Toast.makeText(context, "متن نظر خالی میباشد", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                            case "400": {
                                                Toast.makeText(context, "کاربر یافت نشد", Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(context, "متن نظر خالی میباشد", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                            case "400": {
                                                Toast.makeText(context, "کاربر یافت نشد", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                        }
                                    }
                                });
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
    public int getItemCount() {
        return commentArrayMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, publishtime, comment, reply, report;
        private RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.row_comment_name);
            publishtime = view.findViewById(R.id.row_comment_time);
            comment = view.findViewById(R.id.row_comment_comment);
            reply = view.findViewById(R.id.row_replay);
            report = view.findViewById(R.id.row_report);
            recyclerView = view.findViewById(R.id.rv_search);

        }
    }


}